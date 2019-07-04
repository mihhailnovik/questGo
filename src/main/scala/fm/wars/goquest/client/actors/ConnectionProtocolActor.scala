package fm.wars.goquest.client.actors

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import fm.wars.goquest.client.config.{PlayerTokenManager, WarsFmConfig}
import fm.wars.goquest.client.model.AktorMessages._
import fm.wars.goquest.client.util.MessageHelper
import fm.wars.goquest.client.util.MessageHelper.{authenticate, connectGo9, findGame}

/**
 * This actor is responsible to receiving and replying to server system messages (keep-alive, connected)
 *
 * - Connected. Reply with join 9v9 server and look for a game
 * - Ping. Reply with Pong
 * - User Token. Store it and reply with looking for a game.
 */
class ConnectionProtocolActor(tokenManager: PlayerTokenManager) extends Actor with ServerRefActor {

  override def receive: Receive = LoggingReceive {

    case actor: ActorRef => receiveActor(actor)

    case _: Ping.type => reply(MessageHelper.PING_MESSAGE)

    case _: Connected.type =>
      reply(connectGo9)
      val nextStep = tokenManager.playerToken() match {
        case Some(PlayerToken(username, token)) => findGame(username, token)
        case None => authenticate(WarsFmConfig.password)
      }
      reply(nextStep)

    // TODO persist it somewhere..
    case PlayerToken(username, token) => reply(findGame(username, token))

    case unknown => logger.error(s"Unknown message received $unknown")

  }

}
