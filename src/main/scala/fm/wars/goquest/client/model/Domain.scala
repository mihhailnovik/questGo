package fm.wars.goquest.client.model

import fm.wars.goquest.client.model.Domain.Player.Player

/*
 Domain objects (non aktor messages / non http ) for business logic
 */
object Domain {

  object Player extends Enumeration {
    type Player = Value
    val WHITE, BLACK = Value

    def toStr(p:Player):String = {
      p match {
        case Player.WHITE => "W"
        case Player.BLACK => "B"
      }
    }

    def reverse(p: Player) : Player = {
      p match {
        case Player.WHITE => Player.BLACK
        case Player.BLACK => Player.WHITE
      }
    }
  }

  case class Move(p: Player, move: Option[MoveAction], action: Option[String], moveNr: Int) {
    val color = Player.toStr(p)
    val toStr: Option[String] = move.map(mv => color + "[" + mv.toMove + "]")
  }

  trait MoveAction {
    def toMove: String
  }

  val resignAction = Some("LOSE:RESIGN")

  val passMove = Some(new MoveAction {
    override def toMove: String = ""
  })

  case class MoveCoordinate(move: String, boardSize: Int) extends MoveAction {
    require(move.length == 2, "input string should be in format 'xy'")
    val range = 'a' to ('a' + boardSize).toChar // XXX will not work for really small boards
    require(range.contains(move.charAt(0)), "move is out of board range " + move.charAt(0))
    require(range.contains(move.charAt(1)), "move is out of board range " + move.charAt(1))
    override def toMove: String = move
  }
}
