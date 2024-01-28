ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

ThisBuild / scalaVersion := versions.scalaVersion


lazy val versions = new {
  val scalaTest = "3.2.15"
  val scalaVersion = "2.13.11"
  val pekkoVersion = "1.0.1"
  val pekkoHttpVersion = "1.0.0"
}

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    organization := "com.funny",
    name := "tapir-pekko-hello-world",
    Compile / run / mainClass := Some(s"${organization.value}.Main"),
  )


val commonSettings = Seq (
  libraryDependencies ++= {
    Seq(
      "org.scalatest" %% "scalatest" % versions.scalaTest % "test",
      "com.softwaremill.sttp.tapir" %% "tapir-pekko-http-server" % "1.7.3",
      "org.apache.pekko" %% "pekko-actor-typed" % versions.pekkoVersion,
      "org.apache.pekko" %% "pekko-http" % versions.pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % versions.pekkoHttpVersion,
    )
  },
  scalacOptions ++= Seq("-Ymacro-annotations", "-Xfatal-warnings", "-deprecation", "-unchecked", "-encoding", "utf8"),
)
