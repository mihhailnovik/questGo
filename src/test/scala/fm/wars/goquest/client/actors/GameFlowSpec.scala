package fm.wars.goquest.client.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import fm.wars.goquest.client.config.{PlayerTokenManager, PlayerTokenManagerImpl, WarsFmConfig}
import fm.wars.goquest.client.gtp.ConsoleGameIO
import fm.wars.goquest.client.model.AktorMessages
import fm.wars.goquest.client.util.ProtocolMessages._
import fm.wars.goquest.client.util.WarsActor
import org.scalatest.{BeforeAndAfterAll, WordSpec}

class GameFlowSpec extends WordSpec with BeforeAndAfterAll {

  private implicit val system = ActorSystem("GameFlowSpec")
  "Quest go game client " should {
    val tokenManager = new PlayerTokenManager() {
      override def playerToken(): Option[AktorMessages.PlayerToken] = None
    }

    val connectionProtocol = WarsActor(Props(new ConnectionProtocolActor(tokenManager)))
    val sender = TestProbe()

    val gameController = WarsActor(Props(new GameController(new ConsoleGameIO(){}, WarsFmConfig.userName)))

    val wsClientActor = WarsActor(Props(new WebSocketClientActor(Seq(connectionProtocol), Seq(gameController), tokenManager)))
    wsClientActor ! sender.ref

    "Send game settings message after server reply with connected" in {
      wsClientActor ! SERVER_CONNECTED
      sender expectMsg CLIENT_GAME_SETTINGS
      sender expectMsg CLIENT_PASSWORD
    }

    "Send pong message after it receive ping" in {
      wsClientActor ! SERVER_PING
      sender expectMsg CLIENT_PING
    }

    "Send look for game request after it gets user token " in {
      wsClientActor ! SERVER_AUTH_DATA
      sender expectMsg CLIENT_LOOK_FOR_GAME
    }

    "Send move after game is started " in {
      wsClientActor ! SERVER_GAME_STATE // need ot send game context first
      Thread.sleep(50) // otherwise order not guaranteed
      sender expectMsg CLIENT_DO_MOVE
    }

  }

}
