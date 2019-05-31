name := "streaming_notifications"

version := "0.1"

scalaVersion := "2.12.8"

resolvers ++= Seq(
  "confluent" at "https://packages.confluent.io/maven/",
  Resolver.bintrayRepo("ovotech", "maven")
)


// There's a PackagingTypePlugin found in the project's directory. Please include this to resolve depedencies

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.21",
  "com.typesafe.akka" %% "akka-stream" % "2.5.21",
  "com.typesafe" % "config" % "1.3.4"
) ++ testDeps ++ kafkaDeps ++ circeDeps ++ akkaKafkaStreamsDeps

val testDeps = Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)



val circeDeps = Seq (
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map (_ % "0.10.0")

val akkaKafkaStreamsDeps = Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.3",
  "com.typesafe.akka" %% "akka-stream-kafka-testkit" % "1.0.3"
)

val kafkaDeps = Seq(
  "org.apache.kafka" %% "kafka" % "2.2.0",
  "io.github.embeddedkafka" %% "embedded-kafka" % "2.2.0",
  "io.github.embeddedkafka" %% "embedded-kafka-schema-registry" % "5.2.1",
  "org.glassfish.jersey.bundles.repackaged" % "jersey-guava" % "2.25.1", // Needed
)