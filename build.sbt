ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

ThisBuild / scalaVersion := versions.scalaVersion


lazy val versions = new {
  val scalaTest = "3.2.19"
  val scalaVersion = "2.13.11"
  val pekkoVersion = "1.0.3"
  val pekkoHttpVersion = "1.0.1"
}

fork := true

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    organization := "com.funny",
    name := "Pekko Hello World",
    Compile / run / mainClass := Some(s"${organization.value}.Main"),
  )


val commonSettings = Seq (
  libraryDependencies ++= {
    Seq(
      "org.apache.pekko"    %%  "pekko-http"                 % versions.pekkoHttpVersion,
      "org.apache.pekko"    %%  "pekko-http-spray-json"      % versions.pekkoHttpVersion,
      "org.apache.pekko"    %%  "pekko-actor-typed"          % versions.pekkoVersion,
      "org.apache.pekko"    %%  "pekko-stream"               % versions.pekkoVersion,
      "ch.qos.logback"      %   "logback-classic"            % "1.2.13",
      "org.scalatest"       %%  "scalatest"                  % versions.scalaTest          % Test,
      "org.apache.pekko"    %%  "pekko-http-testkit"         % versions.pekkoHttpVersion   % Test,
      "org.apache.pekko"    %%  "pekko-actor-testkit-typed"  % versions.pekkoVersion       % Test,
    )
  },
  scalacOptions ++= Seq("-Ymacro-annotations", "-Xfatal-warnings", "-deprecation", "-unchecked", "-encoding", "utf8"),
)
