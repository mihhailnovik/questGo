package fm.wars.goquest.client.gtp

import fm.wars.goquest.client.model.Domain
import fm.wars.goquest.client.model.Domain.Player.Player


trait GtpGameIO {
  def genMove(playerColor: Domain.Player.Value): TimedGtpResult
  def playMove(P: Player, coordinate: GtpAction): GtpResult
  def clearBoard(): GtpResult
  def timeSettings(mainTime:Int, byoYomiTime:Int, byoYomiStones:Int): GtpResult
  def name(): GtpResult
  def komi(komi: Int): GtpResult
  def showBoard(): GtpResult
}
