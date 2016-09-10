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

  "org.postgresql" % "postgresql" % "9.4-1202-jdbc42",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.jsoup" % "jsoup" % "1.8.3",
  "commons-validator" % "commons-validator" % "1.5.0",
  "org.passay" % "passay" % "1.1.0",
  "com.typesafe.play" % "play-mailer_2.11" % "5.0.0-M1"
)
