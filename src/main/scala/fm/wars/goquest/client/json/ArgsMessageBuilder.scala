package fm.wars.goquest.client.json

import com.typesafe.scalalogging.LazyLogging
import fm.wars.goquest.client.model.ProtocolModels.{GameFinished, GameFound, GameMessage, GameMessageWithMeta, GameState, GameToken, LookForGame, ServerInfo, Spectators}
import io.circe.Decoder.Result
import io.circe.Json
import io.circe.parser.parse
import CirceCoders._
import fm.wars.goquest.client.util.MessageHelper.MessageGameCodes._

object ArgsMessageBuilder extends LazyLogging {
  def apply(json: String): Option[GameMessageWithMeta] = parseMessage(json)

  private def parseMessage(json: String): Option[GameMessageWithMeta] = {
    def parseOrNone = {
      parse(json) match {
        case Right(json: Json) =>
          val name = (json \\ "name").headOption.flatMap(_.asString).getOrElse("")
          Some(
            GameMessageWithMeta(
              name,
              toContent(name, json \\ "args")))
        case Left(ex) =>
          logger.warn(s"Unable to parse message $json", ex)
          None
      }
    }
    if (json.isEmpty) None else parseOrNone
  }

  private def toContent(name: String, json: List[Json]): Array[GameMessage] = {
    val data: Json = json.head.asArray.head.head
    val result: Result[GameMessage] = name match {
      case AUTHENTICATION => data.as[GameToken]
      case SERVER_INFO => data.as[ServerInfo]
      case GAME_UPDATED => data.as[GameState]
      case SEEK_GAME_PROGRESS => data.as[LookForGame]
      case GAME_FOUND => data.as[GameFound]
      case SPECTATORS_NUMBER => data.as[Spectators]
      case GAME_FINISHED => data.as[GameFinished]
    }
    val arr = result match {
      case Left(value) =>
        logger.error("Cannot parse message ", value)
        Array[GameMessage]()
      case Right(value) => Array[GameMessage](value)
    }
    arr
  }
}
