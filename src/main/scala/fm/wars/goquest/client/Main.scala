package fm.wars.goquest.client

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import fm.wars.goquest.client.actors.{ConnectionProtocolActor, GameController, WebSocketClientActor}
import fm.wars.goquest.client.bindings.Bindings.appInjector
import fm.wars.goquest.client.config.{PlayerTokenManager, WarsFmConfig}
import fm.wars.goquest.client.gtp.GtpLeela
import fm.wars.goquest.client.http.WarsFmClient
import fm.wars.goquest.client.util.WarsActor
import scaldi.Injectable

object Main extends App with Injectable with LazyLogging {

  implicit val actorSystem: ActorSystem = inject[ActorSystem]
  implicit val actorMaterializer: ActorMaterializer = inject[ActorMaterializer]

  val tokenManager = inject[PlayerTokenManager]

  val connectionProtocol = WarsActor(Props(new ConnectionProtocolActor(tokenManager)))
  val leela = new GtpLeela()
  val gameController = WarsActor(Props(new GameController(leela, WarsFmConfig.userName)))
  val wsClientActor = WarsActor(Props(new WebSocketClientActor(Seq(connectionProtocol), Seq(gameController), tokenManager)))
  val wsServerActor = new WarsFmClient().connect(wsClientActor)

}
