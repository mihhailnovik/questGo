package fm.wars.goquest.client

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import fm.wars.goquest.client.actors.AkkaConverter.serverMessageToAkkaWs
import fm.wars.goquest.client.model.ProtocolModels.WarsMessage
import fm.wars.goquest.client.json.CirceCoders._
import scaldi.Injectable
import fm.wars.goquest.client.bindings.Bindings.appInjector

package object actors {
  object AkkaConverter {
    import io.circe.syntax._
    def mkMessage(message: WarsMessage): String = {
      val dots = if (message.code < 3) "::" else ":::"
      s"${message.code}$dots" + message.messageOpt.map(_.asJson.noSpaces).getOrElse("")
    }
    def serverMessageToAkkaWs(message: WarsMessage): Message = TextMessage(mkMessage(message))

  }

  trait ActorSupport extends Injectable {
    implicit val actorSystem: ActorSystem = inject[ActorSystem]
    implicit val actorMaterializer: ActorMaterializer = inject[ActorMaterializer]
  }

  private[actors] trait ServerRefActor extends LazyLogging {

    var server: Option[ActorRef] = None

    def reply(message: WarsMessage): Unit = {
      val converted: Message = serverMessageToAkkaWs(message)
      logger.info(s"GameController -> QuestGoServer: $converted")
      server.foreach(_ ! converted)
      warnIfNoServerSet()
    }

    def receiveActor(actor: ActorRef): Unit = server = Some(actor)

    private def warnIfNoServerSet() = if (server.isEmpty) logger.warn("Server is not yet set")

  }

}
