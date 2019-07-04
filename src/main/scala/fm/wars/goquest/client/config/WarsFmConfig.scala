package fm.wars.goquest.client.config

object WarsFmConfig {

  import com.typesafe.config.Config
  import com.typesafe.config.ConfigFactory

  val conf: Config = ConfigFactory.load
  val host: String = conf.getString("wars.fm.host")
  val password: String = conf.getString("wars.fm.password")
  val httpHost: String = "http://" + host
  val wsHost: String = "ws://" + host + "websocket/"
  val token: Option[String] = getOption("wars.fm.token")
  val userName: Option[String] = getOption("wars.fm.username")

  val leelaPath = conf.getString("leela.path")
  val leelaWeights = conf.getString("leela.weights")
  val leelaSeed: Option[String] = getOption("leela.seed")

  private def getOption(path:String):Option[String] = {
    if (conf.hasPath(path)) {
      Some(conf.getString(path))
    } else {
      None
    }
  }

}
