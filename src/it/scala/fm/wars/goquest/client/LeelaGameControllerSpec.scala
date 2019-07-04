package fm.wars.goquest.client

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import fm.wars.goquest.client.actors.GameController
import fm.wars.goquest.client.config.WarsFmConfig
import fm.wars.goquest.client.gtp.GtpLeela
import fm.wars.goquest.client.model.AktorMessages.{GameStarted, PlayerToken}
import fm.wars.goquest.client.model.ProtocolModels.{GameState, Move, Player, Position}
import fm.wars.goquest.client.util.ProtocolMessages.CLIENT_DO_MOVE_3_LEELA
import fm.wars.goquest.client.util.WarsActor
import org.scalatest.{BeforeAndAfterAll, WordSpec}

import scala.concurrent.duration._
class LeelaGameControllerSpec  extends WordSpec with BeforeAndAfterAll {

  private implicit val system = ActorSystem("LeelaGameControllerSpec")
  val baseGameState = GameState("id","go9", Position(Array[Move](), Some(9)), Array[Player](
    Player("kuroton", "kuroton", 3, 1999),
    Player("nickname", "nickname", 3, 1999)
  ))
  "Game controller leela integration " should {
    val gameController = WarsActor(Props(new GameController(new GtpLeela(), WarsFmConfig.userName)))
    val sender = TestProbe()
    gameController ! sender.ref // this is so we can check gameController moves
    gameController ! PlayerToken("nickname", "mock") // so our ai knows which
    gameController ! GameStarted(9) // set game

    "receive initial state of board with AI playing white color and do nothing " in {
      gameController ! baseGameState
      sender expectNoMessage()
    }

    "receive update about enemy move and make ai move" in {
      gameController ! baseGameState.copy(position = baseGameState.position.copy( Array(Move(1000, "B[ee]"))))
      sender expectMsg(10 seconds, CLIENT_DO_MOVE_3_LEELA)
    }
  }
}
