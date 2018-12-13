name := "sangria-graphql"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq (
  "com.typesafe.akka"   %% "akka-http"       % "10.1.5",
  "com.typesafe.akka"   %% "akka-stream"     % "2.5.12",
  "org.sangria-graphql" %% "sangria"         % "1.4.2",

  "ch.qos.logback"      %  "logback-classic" % "1.2.3",
)