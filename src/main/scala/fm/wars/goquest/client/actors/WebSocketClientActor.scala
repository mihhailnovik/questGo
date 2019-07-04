package fm.wars.goquest.client.actors

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.model.ws.TextMessage.Strict
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import fm.wars.goquest.client.config.{PlayerTokenManager, WarsFmConfig}
import fm.wars.goquest.client.model.AktorMessages._
import fm.wars.goquest.client.model.ProtocolModels._
import fm.wars.goquest.client.util.{MessageHelper, StreamUtil}
import fm.wars.goquest.client.util.MessageHelper.MessageGameCodes._
import fm.wars.goquest.client.util.MessageHelper.MessageSystemCodes._
import fm.wars.goquest.client.util.WarsActor.seqToSeqA

import scala.util.{Failure, Success}

/**
 * This actor is responsible to
 * - receiving websocket text messages, parse into domain object,
  * forward domain object to specific listeners (system info listeners, or game logic listeners)
 * - passing web socket server instance to all listeners
 */
class WebSocketClientActor(val connectionListeners: Seq[ActorRef],
                           val gameInfoListeners: Seq[ActorRef],
                           val tokenManager: PlayerTokenManager)
  extends Actor with LazyLogging with ServerRefActor {
  implicit val materializer = ActorMaterializer()

  val allListeners = connectionListeners ++ gameInfoListeners

  override def receive: Receive = LoggingReceive {
    case webSocketServer: ActorRef =>
      receiveActor(webSocketServer)
      allListeners ! webSocketServer
    case Strict(text) => processRequest(text)
    case TextMessage.Streamed(stream) =>
      import scala.concurrent.ExecutionContext.Implicits.global
      StreamUtil.convertToString(stream).onComplete {
        case Success(text) => processRequest(text)
        case Failure(exception) => logger.error("Cannot consume streamed resources", exception)
      }
  }

  private def processRequest(request: String) = {
    MessageHelper.parse(request) match {
      case Success(warsMessage: WarsMessage) => handleMessage(warsMessage)
      case Failure(exception) => logger.error(s"Error during message[$request] parsing ", exception)
    }
  }

  private def handleMessage(warsMessage: WarsMessage) =
    warsMessage.code match {
      case CONNECTED => connectionListeners ! Connected
      case PING => connectionListeners ! Ping
      case GAME_INFO if warsMessage.messageOpt.exists(_.args.length == 1) => handleGameMessage(warsMessage.messageOpt.get)
      case unknown => logger.error(s"Unknown message code = [$unknown]")
    }

  private def handleGameMessage(message: GameMessageWithMeta) = {
    (message.name, message.args.head) match {
      case (AUTHENTICATION, GameToken(id, _, token)) => allListeners ! PlayerToken(id, token)
      case (SERVER_INFO, _) =>
      // looking for a game message... it's a reply from server that they initiate game search but no game found yet
      case (SEEK_GAME_PROGRESS, LookForGame(_, waiting)) => logger.info("Looking for a game. Waiting " + waiting)
      case (GAME_FOUND, GameFound(_, gtype, _, _)) =>
        logger.info("Game is found")
        gameInfoListeners ! GameStarted(gtype.substring(2).toInt)
      case (GAME_UPDATED, gs) => gameInfoListeners ! gs
      case (SPECTATORS_NUMBER, Spectators(_, num)) => logger.info(s"Spectators: $num")
      case (GAME_FINISHED, GameFinished(id)) =>
        // TODO statistics ? :)
        logger.info(s"Game[$id] finished ")
        startNewGame()
      case unknown => logger.error(s"Unknown message received $unknown")
    }
  }

  private def startNewGame() = {
    tokenManager.playerToken() match {
      case Some(token) =>
        logger.info("Starting a new one... ")
        allListeners ! PlayerToken(token.username, token.token)
      case None => logger.info("Player token is missing. Cannot continue play")
    }
  }
}
