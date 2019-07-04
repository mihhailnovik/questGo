package fm.wars.goquest.client.http

import java.time.Instant

import akka.actor.{ ActorRef, _ }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{ Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse }
import akka.http.scaladsl.model.{ HttpRequest, ResponseEntity, StatusCodes, Uri }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{ Flow, Keep, Sink, Source }
import akka.{ Done, NotUsed }
import com.typesafe.scalalogging.LazyLogging
import fm.wars.goquest.client.actors.ActorSupport
import fm.wars.goquest.client.config.WarsFmConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

trait WarsFmDataExtractor extends LazyLogging {
  ac: ActorSupport =>
  def extractKey(response: String) = {
    logger.debug(s"Extracting key from $response")
    val result = response.split(":").head
    logger.debug(s"Result is $result")
    result
  }
  def extractBody(body: ResponseEntity) = Unmarshal(body).to[String]
}

trait WarsWsClient extends LazyLogging {
  def openWsSocket(sessionId: String) = {
    logger.debug("WebSocketRequest to " + WarsFmConfig.wsHost + sessionId)
    WebSocketRequest(uri = WarsFmConfig.wsHost + sessionId)
  }
}

trait WarsFmHttpClient extends LazyLogging {
  ac: ActorSupport =>
  def requestSessionId = {
    val url = WarsFmConfig.httpHost + "?t=" + Instant.now.getEpochSecond
    logger.debug(s"Sending session request to $url")
    Http().singleRequest(HttpRequest(uri = Uri(url)))
  }
}

class WarsFmClient
  extends ActorSupport
  with WarsFmDataExtractor
  with WarsFmHttpClient
  with WarsWsClient
  with LazyLogging {

  def connect(wsClientActor: ActorRef): ActorRef = {
    val webSocketFlow = Http().webSocketClientFlow(wsHandShakeRequest) // connect to websocket
    val messageSource = Source.actorRef[TextMessage.Strict](bufferSize = 10, OverflowStrategy.fail)
    val messageSink: Sink[Message, NotUsed] =
      Flow[Message]
        .map {
          message =>
            {
              logger.debug(s"QuestGoServer -> WarsFmClient: $message")
              wsClientActor ! message
            }
        }
        .to(Sink.ignore)
    val ((ws, upgradeResponse), _) =
      messageSource
        .viaMat(webSocketFlow)(Keep.both)
        .toMat(messageSink)(Keep.both)
        .run()
    handShakeResult(upgradeResponse)
    logger.debug("Returning actor for sending requests " + ws)
    wsClientActor ! ws // reference to client
    ws
  }

  private def handShakeResult(upgradeResponse: Future[WebSocketUpgradeResponse]) = {
    upgradeResponse.flatMap { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        logger.debug("Success!")
        Future.successful(Done)
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }
  }

  private def wsHandShakeRequest = {
    Await.result(getSessionId.map(openWsSocket), 5.seconds)
  }

  private[client] def getSessionId = {
    requestSessionId
      .map[ResponseEntity](_.entity)
      .flatMap[String](extractBody)
      .map[String](extractKey)
  }
}
