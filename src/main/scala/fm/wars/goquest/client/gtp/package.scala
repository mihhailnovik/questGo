package fm.wars.goquest.client

package object gtp {

  case class TimedGtpResult(time: Long, gtpResult: GtpResult)
  type GtpResult = Either[_, String]

  trait GtpAction {
    def actionCommand: String
  }

  case class GtpCoordinate(x: Char, y:Int) extends GtpAction {
    override def actionCommand: String = x.toString + y.toString
  }

  val passAction = new GtpAction {
    override def actionCommand: String = "pass"
  }

  object Commands {
    val PLAY = "play"
    val GENMOVE = "genmove"
    val NAME = "name"
    val TIME_SETTINGS = "time_settings"
    val CLEAR_BOARD = "clear_board"
    val KOMI = "komi"
  }
}
