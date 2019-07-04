package fm.wars.goquest.client.util

import fm.wars.goquest.client.model.ProtocolModels.GameState

import scala.util.Try

object GameStatePrinter {

  val range = 'a' to 'i'

  def toBoardString(gs: GameState) = {
    Try { // FIXME java.lang.StringIndexOutOfBoundsException: begin 2, end 4, length 3 for pass move W[]
    val moves: Map[String, String] = gs.position.moves.map(_.m).map(
      el => el.substring(2, 4) -> invert(el.substring(0, 1))).toMap
    val stringBuilder = new StringBuilder()
    stringBuilder.append("\n")
    stringBuilder.append("  ")
    range.foreach {
      i =>
        {
          stringBuilder.append(i)
          stringBuilder.append(" ")
        }
    }
    stringBuilder.append("\n")
    range.foreach { i =>
      stringBuilder.append(i)
      stringBuilder.append(" ")
      range.foreach { y =>
        stringBuilder.append(moves.getOrElse(i.toString + y.toString, ".") + " ")
      }
      stringBuilder.append("\n")
    }
    stringBuilder.append("\n")
    stringBuilder.toString
    }.getOrElse("Pass")
  }

  private def invert(s: String) = s match {
    case "W" => "X"
    case "B" => "O"
  }

}