# GoQuest client
GoQuest is an online go server, where people can play go against each other
Web client - http://wars.fm/go9
Android client - https://play.google.com/store/apps/details?id=fm.wars.goquest

The goal of this project:
 - create scala client for questogo server
 - connect questogo scala client and GTP protocol
 - implement it for leela zero for 9x9 board
 - get into top 20

### Prerequisites
You should know basic usages of command line.
- jdk12 (https://jdk.java.net/12/)
- scala 2.12.8 (https://www.scala-lang.org/download/2.12.8.html)
- sbt (https://www.scala-sbt.org/download.html)

### Getting Started

- Register questoGo account (should use mobile app)
- Change configuration secret_password to your secret code (#see account configuration in app)
- remove username & token from configuration if you do not know them (can receive only through API)
- install leela zero https://github.com/ihavnoid/leelaz-ninenine
- download leela weights
- make sure leela is working from ur command line (sh src/leelaz --weights example-data/152s.txt)
- change leela configuration to yours

~~~~
wars.fm {
  host = "wars.fm:3002/socket.io/1/"
  password = "secret_password"
  username = "nickname"
  token = "secret_token"
}

leela {
  path = "/Users/user/Programs/leelaz-ninenine/src/leelaz"
  weights = "/Users/user/Programs/leelaz-ninenine/example-data/152s.txt"
}
~~~~
To run:

sbt run fm.wars.goquest.client.Main
Will start application. It will connect to server and start to play continuously

If any question feel free to ask on github
