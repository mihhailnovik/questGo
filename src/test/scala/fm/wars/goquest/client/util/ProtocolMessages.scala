package fm.wars.goquest.client.util

import akka.http.scaladsl.model.ws.TextMessage

object ProtocolMessages {
  val SERVER_CONNECTED = TextMessage("1::")
  val SERVER_PING = TextMessage("2::")
  val SERVER_AUTH_DATA = TextMessage("5:::{\"name\":\"b9345c05\",\"args\":[{\"id\":\"nickname\", \"name\":\"nickname\", \"bot\":0, \"gtype\":\"go9\", \"token\":\"secret_token\", \"pass\":\"secret_password\"}]}")
  val SERVER_SEARCH_IN_PROGRESS = TextMessage("5:::{\"name\":\"341e8bec\",\"args\":[{\"gtype\":\"go9\",\"waiting\":22}]}")
  val SERVER_GAME_STATE_0 = TextMessage("5:::{\"name\":\"8d7a2124\",\"args\":[{\"id\":\"jne9msngbwrx\",\"gtype\":\"go9\",\"started\":\"2019-06-27T15:38:57.949Z\",\"players\":[{\"id\":\"fastgym24\",\"name\":\"fastgym24\",\"dan\":-1,\"rating\":1637},{\"id\":\"nickname\",\"name\":\"nickname\",\"dan\":2,\"rating\":1840}],\"tcb\":180000,\"tci\":1000,\"remTime\":[180000,180000],\"position\":{\"moves\":[]}}]}")
  val SERVER_GAME_STATE = TextMessage("5:::{\"name\":\"8d7a2124\",\"args\":[{\"id\":\"twv5vljc9lnw\",\"gtype\":\"go9\",\"started\":\"2019-05-31T07:36:20.778Z\",\"players\":[{\"id\":\"kuroton\",\"name\":\"kuroton\",\"dan\":2,\"rating\":1842},{\"id\":\"nickname\",\"name\":\"nickname\",\"dan\":2,\"rating\":1889}],\"tcb\":180000,\"tci\":1000,\"remTime\":[179899,180000],\"position\":{\"moves\":[{\"t\":101,\"m\":\"B[ee]\"}],\"size\":9}}]} ")
  val SERVER_GAME_STATE_2 = TextMessage("5:::{\"name\":\"8d7a2124\",\"args\":[{\"id\":\"twv5vljc9lnw\",\"gtype\":\"go9\",\"started\":\"2019-05-31T07:36:20.778Z\",\"players\":[{\"id\":\"kuroton\",\"name\":\"kuroton\",\"dan\":2,\"rating\":1842},{\"id\":\"nickname\",\"name\":\"nickname\",\"dan\":2,\"rating\":1889}],\"tcb\":180000,\"tci\":1000,\"remTime\":[179899,180000],\"position\":{\"moves\":[{\"t\":101,\"m\":\"B[ee]\"},{\"t\":401,\"m\":\"W[ed]\"},{\"t\":701,\"m\":\"B[ec]\"}],\"size\":9}}]} ")

  val CLIENT_GAME_SETTINGS = TextMessage("5:::{\"name\":\"efa2bd1b\",\"args\":[{\"env\":\"WEB\",\"handicapV\":\"1\",\"gtype\":\"go9\"}]}")
  val CLIENT_PING = TextMessage("2::")
  val CLIENT_PASSWORD = TextMessage("5:::{\"name\":\"b9345c05\",\"args\":[{\"pass\":\"testpassword\",\"gtype\":\"go9\"}]}")
  val CLIENT_LOOK_FOR_GAME = TextMessage("5:::{\"name\":\"49669ea6\",\"args\":[{\"id\":\"nickname\",\"gtype\":\"go9\",\"token\":\"secret_token\"}]}")
  val CLIENT_DO_MOVE = TextMessage("5:::{\"name\":\"dcc52856\",\"args\":[{\"game_id\":\"twv5vljc9lnw\",\"player_id\":\"nickname\",\"ply\":1,\"m\":\"W[ge]\",\"t\":1}]}")
  val CLIENT_DO_MOVE_2 = TextMessage("5:::{\"name\":\"dcc52856\",\"args\":[{\"game_id\":\"twv5vljc9lnw\",\"player_id\":\"nickname\",\"ply\":3,\"m\":\"W[ec]\",\"t\":5000}]}")


  val CLIENT_DO_MOVE_3_LEELA = TextMessage("5:::{\"name\":\"dcc52856\",\"args\":[{\"game_id\":\"id\",\"player_id\":\"nickname\",\"ply\":1,\"m\":\"W[df]\",\"t\":3000}]}")
}
