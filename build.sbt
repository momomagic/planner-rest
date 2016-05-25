name := "planner-server"
organization  := "org.smartplanner"
version := "1.0"

scalaVersion := "2.11.8"


scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.5"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %% "spray-can"      %  sprayV withSources() withJavadoc(),
    "io.spray"            %% "spray-routing" % sprayV withSources() withJavadoc(),
    "io.spray"            %% "spray-client"  % sprayV withSources() withJavadoc(),
    "io.spray"            %%  "spray-json"    % "1.3.2" withSources() withJavadoc(),
    "io.spray"            %% "spray-caching" % sprayV withSources() withJavadoc(),
    "com.datastax.cassandra"  % "cassandra-driver-core" % "3.0.2",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-slf4j"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "3.8" % "test",
    "org.scalaz"          %%  "scalaz-core"   % "7.1.8"
  )
}

fork in run:= true