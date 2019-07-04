package fm.wars.goquest.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fm.wars.goquest.client.config.WarsFmConfig
import fm.wars.goquest.client.http.WarsFmClient
import org.specs2.mutable.Specification

class WarsFmClientSpec extends Specification {

  "WarsFmConfig" should {
    "read host configuration " in {
      WarsFmConfig.host must beEqualTo("localhost:3002/socket.io/1/websocket/")
    }
  }

  "WarsFmClient" should {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    val client = new WarsFmClient()
    "parse session id from response" in {
      val sessionId = "rvaa001xJRqGYzjcvRT4"
      val httpResponse = s"$sessionId:60:60:websocket,flashsocket,htmlfile,xhr-polling,jsonp-polling"
      client.extractKey(httpResponse) must beEqualTo(sessionId)
    }

  }

}
