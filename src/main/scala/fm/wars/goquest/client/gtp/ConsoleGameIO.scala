package fm.wars.goquest.client.gtp

import fm.wars.goquest.client.model.Domain
import fm.wars.goquest.client.model.Domain.Player.Player
// TODO the idea of this class was to implement command line client.. however now it's just mock for tests
class ConsoleGameIO extends GtpGameIO {

  override def genMove(playerColor: Domain.Player.Value): TimedGtpResult = TimedGtpResult(1, Right("G5"))

  override def playMove(P: Player, coordinate: GtpAction): GtpResult = Right("G5")

  override def clearBoard(): GtpResult = ???

  override def timeSettings(mainTime: Int, byoYomiTime: Int, byoYomiStones: Int): GtpResult = ???

  override def name(): GtpResult = ???

  override def showBoard(): GtpResult = ???

  override def komi(komi: Int): GtpResult = ???
}
