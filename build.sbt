name := "sangria-graphql"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq (
  "org.sangria-graphql" %% "sangria"            % "1.4.2",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.1",
  "com.typesafe.akka"   %% "akka-http-spray-json" % "10.1.6",

  "com.typesafe.akka"   %% "akka-http"          % "10.1.6",
  "com.typesafe.akka"   %% "akka-stream"        % "2.5.19",

  "ch.qos.logback"      %  "logback-classic"    % "1.2.3"
)