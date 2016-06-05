import sbt._
import Keys._

name := "planner-server"
organization  := "org.smartplanner"
version := "1.0"

scalaVersion := "2.11.8"
mainClass in assembly := Some("hacora.planner.receiver.Boot")
assemblyJarName in assembly := "Planner.jar"
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

assemblyMergeStrategy in assembly := {
  case x if x.startsWith("META-INF") => MergeStrategy.discard
  case x if x.contains("slf4j-api") => MergeStrategy.last
  case x if x.contains("io.netty.versions") => MergeStrategy.first
  case PathList("reference.conf") => MergeStrategy.concat
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

libraryDependencies ++= {
  val akkaV = "2.4.5"
  val sprayV = "1.3.3"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-client" % sprayV,
    "io.spray" %% "spray-json" % "1.3.2",
    "io.spray" %% "spray-caching" % sprayV,
    "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.2" exclude("org.xerial.snappy", "snappy-java"),
    "org.xerial.snappy" % "snappy-java" % "1.1.2.4"
  )
}

fork := true