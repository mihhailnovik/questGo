package fm.wars.goquest.client.util

import fm.wars.goquest.client.gtp.GtpCoordinate
import org.specs2.mutable.Specification

class GtpSgfUtilSpec extends Specification {

  "GtpSgfUtil" should  {
    "convert sgf to gtp" in {
      GtpSgfUtil.sgfToGtp('i','e') should_== GtpCoordinate('j', 5)
    }

    "conver gtp to sgf" in {
      GtpSgfUtil.gtpToSgf(GtpCoordinate('j', 5)) should_==  "ie"
    }

    "conver gtp to sgf should convert 9 to i and j to i for sgf " in {
      GtpSgfUtil.gtpToSgf(GtpCoordinate('c', 9)) should_==  "ci"
      GtpSgfUtil.gtpToSgf(GtpCoordinate('j', 6)) should_==  "if"
    }

    "conver sgf to gtp should convert i to 9" in {
      GtpSgfUtil.sgfToGtp('c', 'i') should_==  GtpCoordinate('c', 9)
      GtpSgfUtil.sgfToGtp('i', 'f') should_==  GtpCoordinate('j', 6)
    }


  }
}
