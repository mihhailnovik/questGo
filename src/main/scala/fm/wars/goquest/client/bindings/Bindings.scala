package fm.wars.goquest.client.bindings

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fm.wars.goquest.client.config.{PlayerTokenManager, PlayerTokenManagerImpl}
import scaldi.Module

object Bindings {

  implicit lazy val appInjector = {
    new ActorModule :: new ServiceModule
  }

  class ActorModule extends Module {
    implicit val as: ActorSystem = ActorSystem()
    bind[ActorSystem] to as
    bind[ActorMaterializer] to ActorMaterializer()
  }

  class ServiceModule extends Module {
    bind[PlayerTokenManager] to new PlayerTokenManagerImpl()
  }

}
