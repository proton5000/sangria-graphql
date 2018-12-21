name := "sangria-graphql"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq (
  "org.sangria-graphql" %% "sangria"         % "1.4.2",
  "org.sangria-graphql" %% "sangria-circe"   % "1.2.1",

  "ch.qos.logback"      %  "logback-classic" % "1.2.3"
)