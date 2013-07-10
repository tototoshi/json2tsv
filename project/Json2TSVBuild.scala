import sbt._
import sbt.Keys._

object Json2TSVBuild extends Build {

  lazy val root = Project(
    id = "json2tsv",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "json2tsv",
      organization := "com.github.tototoshi",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0",
      libraryDependencies ++= Seq(
        "org.json4s" %% "json4s-native" % "3.1.0",
        "com.jsuereth" %% "scala-arm" % "1.3",
        "com.github.scopt" %% "scopt" % "3.1.0",
        "org.scalatest" %% "scalatest" % "1.9.1" % "test"
      )
    )
  )
}
