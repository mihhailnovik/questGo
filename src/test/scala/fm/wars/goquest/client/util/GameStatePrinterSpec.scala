package fm.wars.goquest.client.util

import fm.wars.goquest.client.model.ProtocolModels._
import org.specs2.mutable.Specification

class GameStatePrinterSpec extends Specification {
  "Message parser " should {
    "Print border based on GameState object " in {
      val gameState = GameState("iii","go9", Position(Array(
        Move(0, "B[ee]"),
        Move(0, "W[ec]"),
        Move(0, "B[eg]"),
        Move(0, "W[cd]"),
        Move(0, "B[cf]"),
        Move(0, "W[gd]"),
        Move(0, "B[dc]"),
        Move(0, "W[dd]"),
        Move(0, "B[fc]"),
        Move(0, "W[fd]"),
      ), Option(9)), Array())

      val result = GameStatePrinter.toBoardString(gameState)
      println(result)
      result should_== expectedResult
    }
  }


  val expectedResult = "\n  a b c d e f g h i \na . . . . . . . . . \nb . . . . . . . . . \nc . . . X . O . . . \nd . . O X . . . . . \ne . . X . O . O . . \nf . . O X . . . . . \ng . . . X . . . . . \nh . . . . . . . . . \ni . . . . . . . . . \n\n"
}