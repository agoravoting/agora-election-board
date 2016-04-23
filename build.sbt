name := """agora-election-board"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
  "com.chuusai" %% "shapeless" % "2.2.5",
  "com.iheart" %% "play-swagger" % "0.2.2-PLAY2.5",
  "org.webjars" % "swagger-ui" % "2.1.4"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

PlayKeys.devSettings := Seq("play.server.http.port" -> "9500", "play.server.http.address" -> "0:0:0:0:0:0:0:0")

fork in run := false