package fm.wars.goquest.client.model

import java.util

// models to communicate with go server (represent json models)

object ProtocolModels {

  /**
   * Every wars.fm message has format
   * ${messageTypeCode}:::{"name":${messageName},"args":[${message}]}
   *
   * - messageTypeCode is to distinguish between system messages (e.g Connectect (0) / Ping (1) ).
   * All game logic messages has code 5
   * - messageName is unique code for game messages (new move made, player found, .. )
   */
  case class WarsMessage(code: Int, messageOpt: Option[GameMessageWithMeta])

  /**
   * @param name - unique name for message. Based on this name we can tell what format we can expect in args
   * @param args - game message itself
   */
  case class GameMessageWithMeta(name: String, args: Array[GameMessage])

  sealed trait GameMessage

  case class ServerInfo(connected: Int, games: Int, down: DownInfo) extends GameMessage

  case class DownInfo(go19: Option[Int], go9: Option[Int])

  case class GameStart(env: String, handicapV: String, gtype: String) extends GameMessage

  case class Password(pass: String, gtype: String) extends GameMessage

  case class GameToken(id: String, gtype: String, token: String) extends GameMessage

  case class LookForGame(gtype: String, waiting: Int) extends GameMessage

  case class GameState(id: String, gtype: String, position: Position, players: Array[Player]) extends GameMessage {
    override def toString: String = s"GameState(id[$id], gtype[$gtype], position[$position], players[${players.mkString}]"
  }

  case class GameFound(id: String, gtype: String, position: Position, players: Array[Player]) extends GameMessage

  case class GameFinished(id: String) extends GameMessage

  case class Spectators(id: String, num: Int) extends GameMessage

  case class Player(id: String, name: String, dan: Int, rating: Int)

  case class Move(t: Int, m: String)

  case class Position(moves: Array[Move], size: Option[Int]){
    override def toString: String = s"Position(moves[${moves.mkString}], size[$size])"
  }

  // json property names are different (look CirceCoders)
  case class DoMove(gameId: String, playerId: String, moveNumber: Int,
                    move: Option[String], action:Option[String], time: Long) extends GameMessage

}

