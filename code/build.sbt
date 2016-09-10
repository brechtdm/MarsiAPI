name := """MarsiAPI"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  filters,
  javaWs,
  evolutions,

  "com.typesafe.play" % "play-mailer_2.11" % "5.0.0-M1",
  "commons-validator" % "commons-validator" % "1.5.0",
  "io.jsonwebtoken" % "jjwt" % "0.6.0",
  "org.jsoup" % "jsoup" % "1.8.3",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.passay" % "passay" % "1.1.0",
  "org.postgresql" % "postgresql" % "9.4-1202-jdbc42"
)
