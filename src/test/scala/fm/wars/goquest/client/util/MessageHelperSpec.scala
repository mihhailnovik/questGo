package fm.wars.goquest.client.util

import fm.wars.goquest.client.actors.AkkaConverter
import fm.wars.goquest.client.model.Domain
import fm.wars.goquest.client.model.Domain.{ MoveCoordinate, Player }
import fm.wars.goquest.client.model.ProtocolModels._
import fm.wars.goquest.client.util.MessageHelper.MessageGameCodes
import org.specs2.matcher.ThrownMessages
import org.specs2.mutable.Specification

import scala.util.{ Failure, Success }

class MessageHelperSpec extends Specification with ThrownMessages {

  "Message parser " should {

    "parse ping message [1::]" in {
      MessageHelper.parse("1::") match {
        case Success(ping) => ping.code == 1 && ping.messageOpt.isEmpty
        case _ => false
      }
    }

    "parse ping message [2::]" in {
      MessageHelper.parse("2::") match {
        case Success(ping) => ping.code == 2 && ping.messageOpt.isEmpty
        case _ => false
      }
    }

    "parse game search in progress message " in {
      MessageHelper.parse(ProtocolMessages.SERVER_SEARCH_IN_PROGRESS.text) match {
        case Success(gameState) => gameState.code == 5 && gameState.messageOpt.isDefined
        case Failure(_) => false
      }
    }

    "parse game state message " in {
      MessageHelper.parse(ProtocolMessages.SERVER_GAME_STATE.text) match {
        case Success(gameState) =>
          gameState.code should_== 5
          gameState.messageOpt match {
            case Some(GameMessageWithMeta(name, arr)) => {
              name should_== MessageGameCodes.GAME_UPDATED
              arr.length should_== 1
              arr(0) match {
                case GameState(id, gtype, _, players) =>
                  id should_== "twv5vljc9lnw"
                  gtype should_== "go9"
                  players(1).name must_== "nickname"
                case message => failure("invalid message type " + message)
              }
            }
            case other => failure("invalid message result " + other)
          }
          gameState.code == 5 && gameState.messageOpt.isDefined // better checks for actual data
        case _ => false
      }
    }

    "parse server info object " in {
      val args = "{\"connected\":449,\"games\":105,\"down\":{\"go19\":1}}"
      val serverMessage = "5:::{\"name\":\"f19683a8\",\"args\":[" + args + "]}"
      MessageHelper.parse(serverMessage) match {
        case Success(message) =>
          message.code must_== 5
          message.messageOpt must beSome[GameMessageWithMeta]
          message.messageOpt.get.name must_== MessageGameCodes.SERVER_INFO
          message.messageOpt.get.args.length must_== 1
          message.messageOpt.get.args.head.asInstanceOf[ServerInfo].games must_== 105
        case Failure(x) => fail("Cannot parse object " + x.getMessage)
      }
    }

    "build Pong message object and return it as text " in {
      val pong = MessageHelper.PING_MESSAGE
      AkkaConverter.mkMessage(pong) must beEqualTo("2::")
    }

    "build request game info " in {
      val request = MessageHelper.connectGo9
      AkkaConverter.mkMessage(request) must
        beEqualTo("5:::{\"name\":\"efa2bd1b\",\"args\":[{\"env\":\"WEB\",\"handicapV\":\"1\",\"gtype\":\"go9\"}]}")
    }

    "build make move request " in {
      val moveCoordinate = MoveCoordinate("ge", 9)
      val move = MessageHelper.makeMove("twv5vljc9lnw", "nickname", Domain.Move(Player.WHITE, Some(moveCoordinate), None, 1), 5000)
      AkkaConverter.mkMessage(move) must
        beEqualTo("5:::{\"name\":\"dcc52856\",\"args\":[{\"game_id\":\"twv5vljc9lnw\",\"player_id\":\"nickname\",\"ply\":1,\"m\":\"W[ge]\",\"t\":5000}]}")
    }

    "return exception for unknown format message" in {
      MessageHelper.parse("[]ewrwr][]") match {
        case Success(_) => false
        case _ => true
      }
    }
  }

}
