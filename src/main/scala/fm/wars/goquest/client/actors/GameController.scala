package fm.wars.goquest.client.actors

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import fm.wars.goquest.client.gtp.{GtpCoordinate, GtpGameIO}
import fm.wars.goquest.client.model.AktorMessages.{GameStarted, PlayerToken}
import fm.wars.goquest.client.model.Domain
import fm.wars.goquest.client.model.Domain.Player.Player
import fm.wars.goquest.client.model.Domain.{MoveCoordinate, Player}
import fm.wars.goquest.client.model.ProtocolModels.{GameState, Move}
import fm.wars.goquest.client.util.GtpSgfUtil.gtpToSgf
import fm.wars.goquest.client.util.{GameStatePrinter, GtpSgfUtil, MessageHelper}

class GameController(val gameIO: GtpGameIO, var userNameOpt: Option[String]) extends Actor with ServerRefActor {

  override def receive: Receive = LoggingReceive {
    case actor: ActorRef => receiveActor(actor) // receive http server actor so we can reply directly to server
    case gs: GameState => ifContextInitialized(id => {
      handleGameUpdate(gs, id)
    })
    case GameStarted(_) => resetGameIO() // new game
    case PlayerToken(userName, _) => userNameOpt = Option(userName) // set context.
  }

  private def handleGameUpdate(gs: GameState, playerId: String) = {
    val playerColor = findPlayerColor(gs, playerId)
    val enemyColor = Player.reverse(playerColor)
    logger.info(s"playerColor $playerColor")

    if (isAiTurn(gs.position.moves, playerColor)) {
      sendEnemyMoveToAi(gs, enemyColor)
      replyAiMove(playerColor, playerId, gs)
    } else {
      logger.info(GameStatePrinter.toBoardString(gs))
    }
  }

  private def replyAiMove(playerColor: Player, userName: String, gs: GameState) = {
    val result = gameIO.genMove(playerColor)
    result.gtpResult match {
      case Right(move) => convertAiMove(move, playerColor, userName, gs, result.time)
      case Left(errorMessage) => logger.error("Ai Genmove error", errorMessage)
    }
  }

  private def toQuestGoMove(move: String, playerColor: Player, moveNr: Int, boardSize: Int) = {
    move match {
      case m if m.contains("resign") => Domain.Move(playerColor, None, Domain.resignAction, moveNr)
      case m if m.contains("pass") => Domain.Move(playerColor, Domain.passMove, None, moveNr)
      case _ =>
        val gtpCord = GtpCoordinate(move(0), move(1).toString.toInt)
        val moveCoord = MoveCoordinate(gtpToSgf(gtpCord), boardSize)
        Domain.Move(playerColor, Some(moveCoord), None, moveNr)
    }
  }

  private def convertAiMove(move: String, playerColor: Player, userName: String, gs: GameState, time: Long) = {
    val moveNr = gs.position.moves.length
    val questGoMove = toQuestGoMove(move, playerColor, moveNr, gs.gtype.substring(2).toInt)
    reply(MessageHelper.makeMove(gs.id, userName, questGoMove, time))
  }

  private def resetGameIO(): Unit = {
    logger.info("Reset gameIO to 7 komi, 3 min main time. 3 sec byoyomi")
    gameIO.clearBoard() // for now we just support 9x9.. that's why not set
    gameIO.timeSettings(180, 3, 1) // FIXME to change later 1 move = 8 seconds max
    gameIO.komi(7)
  }

  private def sendEnemyMoveToAi(gs: GameState, enemyColor: Player) = {
    gs.position.moves.lastOption.map(_.m) foreach {
      lastMove => {
        gameIO.playMove(enemyColor, GtpSgfUtil.sgfToGtpMove(lastMove))
      }
    }
  }

  private def findPlayerColor(gs: GameState, userName: String) = {
    if (gs.players(1).name == userName) { // second player is white
      Player.WHITE
    } else {
      Player.BLACK
    }
  }

  private def isAiTurn(moves: Array[Move], playerColor: Player) = {
    val lastMove = moves.lastOption.map(_.m).map(_ (0).toString)
    lastMove match {
      case Some(value) if value.contains(Player.toStr(playerColor)) => false // if last move is ours is not our turn
      case None => playerColor == Player.BLACK // if no moves made yet.. if we are black then it's our move
      case _ => true
    }
  }

  // executes only if both server and player is set
  private def ifContextInitialized(execution: String => Unit) = {
    val result = for {
      id <- userNameOpt
      _ <- server
    } yield execution(id)
    result match {
      case Some(_) =>
      case None => logger.error("Game controller is not ready, no cotext is set.. missing player/server")
    }
  }
}
