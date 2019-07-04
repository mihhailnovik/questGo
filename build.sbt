val akkaHttpVersion = "10.1.7"
val akkaVersion    = "2.5.21"
val circeVersion = "0.10.0"
val monocleVersion = "1.5.0"

lazy val commonSettings = Seq(
  name            := "questGo",
  organization    := "fm.wars",
  scalaVersion    := "2.12.8"
)
parallelExecution in Test := false

lazy val root = (project in file(".")).
  settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"              % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"   % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"          % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"            % akkaVersion,
      "io.circe"          %% "circe-core"             % circeVersion,
      "io.circe"          %% "circe-generic"          % circeVersion,
      "io.circe"          %% "circe-parser"           % circeVersion,
      "io.circe"          %% "circe-generic-extras"   % circeVersion,
      "org.scaldi"        %% "scaldi"                 % "0.5.8",
      "com.typesafe"      %  "config"                 % "1.3.2",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback"    %  "logback-classic"        % "1.2.3",
      "com.typesafe.akka" %% "akka-http-testkit"      % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"           % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"    % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"              % "3.0.5"         % Test,
      "org.specs2"        %% "specs2-core"            % "4.5.1"         % Test,
    )
  )
