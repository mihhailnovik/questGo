package fm.wars.goquest.client.json

import fm.wars.goquest.client.model.ProtocolModels._
import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder, _ }

object CirceCoders {
  import io.circe.syntax._

  // i didn't find a better way.. both auto and semiauto solution cannot generate wrapped json
  // e.g {"foo":"bar"} instead of {"class": { "foo":"bar"} }
  implicit val gameMessageEncoder: Encoder[GameMessage] = {
    case gs: GameStart => gs.asJson
    case sfg: GameToken => sfg.asJson
    case pw: Password => pw.asJson
    case doMove: DoMove => doMove.asJson
    case gState: GameState => gState.asJson
    case serverInfo: ServerInfo => serverInfo.asJson
    case lookForGame: LookForGame => lookForGame.asJson
    case gameFinished: GameFinished => gameFinished.asJson
    case gameFound: GameFound => gameFound.asJson
    case spectators: Spectators => spectators.asJson
  }

  implicit val gameMessageDecoder: Decoder[GameMessage] = deriveDecoder

  implicit val gameStartEncoder: Encoder[GameStart] = deriveEncoder
  implicit val gameStartDecoder: Decoder[GameStart] = deriveDecoder

  implicit val seekForGameEncoder: Encoder[GameToken] = deriveEncoder
  implicit val seekForGameDecoder: Decoder[GameToken] = deriveDecoder

  implicit val downInfoEncoder: Encoder[DownInfo] = deriveEncoder
  implicit val downInfoDecoder: Decoder[DownInfo] = deriveDecoder

  implicit val serverInfoEncoder: Encoder[ServerInfo] = deriveEncoder
  implicit val serverInfoDecoder: Decoder[ServerInfo] = deriveDecoder

  implicit val namedArgsEncoder: Encoder[GameMessageWithMeta] = deriveEncoder
  implicit val namedArgsDecoder: Decoder[GameMessageWithMeta] = deriveDecoder

  implicit val moveEncoder: Encoder[Move] = deriveEncoder
  implicit val moveDecoder: Decoder[Move] = deriveDecoder

  implicit val positionEncoder: Encoder[Position] = deriveEncoder
  implicit val positionDecoder: Decoder[Position] = deriveDecoder

  implicit val gameStateEncoder: Encoder[GameState] = deriveEncoder
  implicit val gameStateDecoder: Decoder[GameState] = deriveDecoder

  implicit val gameFoundEncoder: Encoder[GameFound] = deriveEncoder
  implicit val gameFoundDecoder: Decoder[GameFound] = deriveDecoder

  implicit val gameFinishedEncoder: Encoder[GameFinished] = deriveEncoder
  implicit val gameFinishedDecoder: Decoder[GameFinished] = deriveDecoder

  implicit val passwordEncoder: Encoder[Password] = deriveEncoder
  implicit val passwordDecoder: Decoder[Password] = deriveDecoder

  // unfortunately deriveEncoder cannot skip empty options
  // https://github.com/circe/circe/issues/836
  implicit val doMoveEncoder: Encoder[DoMove] = (doMove: DoMove) => Json.obj(
    ("game_id", Json.fromString(doMove.gameId)),
    ("player_id", Json.fromString(doMove.playerId)),
    ("ply", Json.fromInt(doMove.moveNumber)),
    if (doMove.move.isDefined) {
      ("m", Json.fromString(doMove.move.get))
    }
    // if m is not defined then s must be defined
    else {
      ("s", Json.fromString(doMove.action.get))
    },
    ("t", Json.fromLong(doMove.time))
  )
  implicit val doMoveDecoder: Decoder[DoMove] = deriveDecoder

  implicit val playerEncoder: Encoder[Player] = deriveEncoder
  implicit val playerDecoder: Decoder[Player] = deriveDecoder

  implicit val lookForGameEncoder: Encoder[LookForGame] = deriveEncoder
  implicit val lookForGameDecoder: Decoder[LookForGame] = deriveDecoder

  implicit val spectatorsEncoder: Encoder[Spectators] = deriveEncoder
  implicit val spectatorsDecoder: Decoder[Spectators] = deriveDecoder
}
