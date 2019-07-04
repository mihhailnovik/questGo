package fm.wars.goquest.client.util
import akka.stream.Materializer
import akka.stream.scaladsl.Source

import scala.concurrent.duration._

object StreamUtil {

  private val MAX_FRAMES = 100

  def convertToString(textStream: Source[String, _])(implicit materializer: Materializer): concurrent.Future[String] = {
    textStream.limit(MAX_FRAMES).completionTimeout(5 seconds).runFold("")(_ + _)
  }

}
