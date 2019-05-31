package com.jimmy.code

import akka.actor.ActorSystem
import akka.kafka.testkit.scaladsl.EmbeddedKafkaLike
import akka.stream.testkit.scaladsl.StreamTestKit.assertAllStagesStopped
import akka.stream.{ActorMaterializer, Materializer}
import io.circe.generic.auto._
import io.circe.syntax._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.BeforeAndAfterAll
import org.scalatest.time._

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContextExecutor

class TwitterConsumerSpec
    extends SpecBase(1234)
    with EmbeddedKafkaLike
    with TwitterConsumerFixture
    with BeforeAndAfterAll {

  override def bootstrapServers: String = "localhost:1234"

  override implicit val patienceConfig = PatienceConfig(
    timeout = scaled(Span(3, Seconds))
  )

  "TwitterConsumer.startConsuming" should "read messages from Kafka and prints them out" in {

    val topicName = "tests-tweets"

    assertAllStagesStopped {

      produceString(topicName, tweets.map { tweet =>
        println(s"Tweet: $tweet")
        tweet.asJson.noSpaces
      })

      implicit val system: ActorSystem = ActorSystem("twitter-consumer")
      implicit val mat: Materializer = ActorMaterializer()
      implicit val ec: ExecutionContextExecutor = system.dispatcher

      val consumerSettings =
        consumerDefaults
          .withGroupId("group1")
          .withBootstrapServers(bootstrapServers)
          .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      TwitterConsumer.startConsuming(consumerSettings, topicName)

    }
  }

}

trait TwitterConsumerFixture {

  val keyDeserializer = new StringDeserializer
  val valueDeserializer = new StringDeserializer

  case class Tweet(user: String, timeStamp: Long, body: String)

  val tweets = Seq(
    Tweet("jimmy", 1000L, "This is some tweet"),
    Tweet("sam", 1003L, "This is another tweet"),
    Tweet("sam", 1032L, "I like cats #cats4life"),
    Tweet("sam", 1042L, "I like dogs #dogs4life")
  )
}
