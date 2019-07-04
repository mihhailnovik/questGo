package fm.wars.goquest.client

import org.specs2.execute.{Result, StandardResults}
import org.specs2.matcher.ValueCheck

object ItTestHelpers {

  val isGtpMove = new ValueCheck[String] {
    def check:    String => Result = (t: String) => if (t.length == 2) StandardResults.success else StandardResults.failure
    def checkNot: String => Result = (t: String) => if (t.length != 2) StandardResults.failure else StandardResults.success
  }

}
