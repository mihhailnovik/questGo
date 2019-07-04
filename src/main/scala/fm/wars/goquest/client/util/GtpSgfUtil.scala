package fm.wars.goquest.client.util
import fm.wars.goquest.client.gtp._

object GtpSgfUtil {

  def sgfToGtpMove(move:String): GtpAction = {
    if (move.length > 4) {
      sgfToGtp(move(2), move(3))
    } else {
      passAction
    }
  }

  def sgfToGtp(x: Char, y: Char) = GtpCoordinate(maybeSwap(x), maybeSwap(y) - 96 - (if (y == 'i') 1 else 0))

  def gtpToSgf(gtpCoordinate: GtpCoordinate) = normalize(gtpCoordinate.x) + normalizeY(gtpCoordinate.y)


  // FIXME those all are dirty hacks to use 'j' instead of 'i' for gtp protocol (there's no 'i' in GTP)
  // look GtpSgfUtilSpec to understand better
  private def maybeSwap(ch: Char): Char = {
    ch match {
      case 'i' => 'j'
      case a => a
    }
  }
  private def normalizeY(ch: Int) = normalize((ch + 96).toChar)
  private def normalize(ch: Char) = ch.toString.toLowerCase.replace('j','i')

}
