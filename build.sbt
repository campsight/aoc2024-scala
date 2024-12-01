import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "scala-seed",
    libraryDependencies += munit % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %% "core" % "3.8.15",
  "com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.8.15",
  "org.slf4j" % "slf4j-nop" % "2.0.9" // Suppress logging warnings
)
