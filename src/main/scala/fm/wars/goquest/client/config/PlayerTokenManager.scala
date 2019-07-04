package fm.wars.goquest.client.config

import fm.wars.goquest.client.model.AktorMessages.PlayerToken

trait PlayerTokenManager {
  def playerToken():Option[PlayerToken]
}

class PlayerTokenManagerImpl extends PlayerTokenManager {
  override def playerToken(): Option[PlayerToken] = {
    for {
      user <- WarsFmConfig.userName
      tok <- WarsFmConfig.token
    } yield PlayerToken(user, tok)
  }
}
