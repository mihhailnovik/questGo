package fm.wars.goquest.client

import fm.wars.goquest.client.gtp.{GtpCoordinate, GtpLeela}
import fm.wars.goquest.client.model.Domain.Player
import org.specs2.mutable.Specification

// todo pass move
// todo resign move
class GtpLeelaSpec extends Specification {
  sequential
  val byoYomiTime = 5
  val leela = new GtpLeela()
  "Gtp leela support following commands " should {
    "name" in {
      leela.name() must beRight("Leela Zero")
    }

    "time_settings" in {
      leela.timeSettings(0, byoYomiTime, 1) must beRight("")
    }

    "play black e5 " in {
      leela.playMove(Player.BLACK, GtpCoordinate('e', 5)) must beRight("")
    }

    "play illegal move returns error" in {
      leela.playMove(Player.BLACK, GtpCoordinate('z', 25)) must beLeft
    }

    "genmove {color}" in {
      val time = System.currentTimeMillis() + 100 // 100 ms for delay..
      val move = leela.genMove(Player.WHITE)
      move.gtpResult must beRight(ItTestHelpers.isGtpMove)
      move.time must beGreaterThan(System.currentTimeMillis() - time)
    }

  }
}
