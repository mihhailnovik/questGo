package fm.wars.goquest.client.util

import fm.wars.goquest.client.json.ArgsMessageBuilder
import fm.wars.goquest.client.model.Domain
import fm.wars.goquest.client.model.ProtocolModels._
import fm.wars.goquest.client.util.MessageHelper.MessageGameCodes.GAME_START
import fm.wars.goquest.client.util.MessageHelper.MessageSystemCodes._

import scala.util.Try

object MessageHelper {

  object MessageSystemCodes {
    val CONNECTED = 1
    val PING = 2
    val GAME_INFO = 5
  }

  val PING_MESSAGE = WarsMessage(PING, None)

  object MessageGameCodes {
    val AUTHENTICATION = "b9345c05"
    val GAME_START = "efa2bd1b"
    val FIND_GAME = "49669ea6"
    val SERVER_INFO = "f19683a8"
    val SEEK_GAME_PROGRESS = "341e8bec"
    val GAME_FOUND = "ba276087"
    val GAME_UPDATED = "8d7a2124"
    val MAKE_MOVE = "dcc52856"
    val SPECTATORS_NUMBER = "d40a7297"
    val GAME_FINISHED = "1f566a1a"
  }

  /**
   * Message itself starts with code + json. Need to extract code and then parse json and put into WarsMessage object
   */
  val messagePattern = "([\\d]):+(.*)".r

  def parse(message: String): Try[WarsMessage] = {
    Try {
      val messagePattern(code, json) = message
      WarsMessage(code.toInt, ArgsMessageBuilder(json))
    }
  }

  private def baseMessage(id: String, gameMessage: GameMessage) =
    WarsMessage(GAME_INFO, Some(GameMessageWithMeta(id, Array(gameMessage))))

  def connectGo9: WarsMessage = baseMessage(GAME_START, GameStart("WEB", "1", "go9"))

  def authenticate(password: String): WarsMessage =
    baseMessage(MessageGameCodes.AUTHENTICATION, Password(password, "go9"))

  def findGame(username: String, token: String): WarsMessage =
    baseMessage(MessageGameCodes.FIND_GAME, GameToken(username, "go9", token))

  /**
    * @param time is how many ms player spent on move
    */
  def makeMove(gameId: String, playerId: String, move: Domain.Move, time: Long): WarsMessage =
    baseMessage(MessageGameCodes.MAKE_MOVE, DoMove(gameId, playerId, move.moveNr, move.toStr, move.action, time))

}

