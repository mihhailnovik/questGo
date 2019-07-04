package fm.wars.goquest.client.model

/*
  Aktor messages objects
 */
object AktorMessages {

  sealed trait WarsServerMessage

  case object Connected extends WarsServerMessage

  case object Ping extends WarsServerMessage

  case class PlayerToken(username: String, token: String) extends WarsServerMessage

  case class GameStarted(size : Int) extends WarsServerMessage

}
