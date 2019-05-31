package com.jimmy.code.tutorials

import akka.kafka.scaladsl.Consumer
import akka.kafka.testkit.scaladsl.EmbeddedKafkaLike
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.testkit.scaladsl.StreamTestKit._
import com.jimmy.code.SpecBase
import io.circe.generic.auto._
import io.circe.syntax._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.BeforeAndAfterAll

import scala.collection.immutable.Seq

class EmbeddedKafkaSandBoxSpec
    extends SpecBase(kafkaPort = 1234)
    with BeforeAndAfterAll
    with EmbeddedKafkaLike
    with EmbeddedKafkaSandBoxFixture {

  "Example" should "something" in {

    assertAllStagesStopped {

      produceString(topicName, tweets.map { tweet =>
        println(s"Tweet: $tweet")
        tweet.asJson.noSpaces
      })

      implicit val stringDeserializer: StringDeserializer =
        new StringDeserializer

      val consumerConfig =
        system.settings.config.getConfig("akka.kafka.consumer")
      val consumerSettings =
        ConsumerSettings(consumerConfig,
                         new StringDeserializer,
                         new StringDeserializer)
          .withBootstrapServers(bootstrapServers)
          .withGroupId("group1")
          .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      Consumer
        .plainSource(consumerSettings, Subscriptions.topics(topicName))
        .map { record: ConsumerRecord[String, String] =>
          println(s"Some value: ${record.value}")
        }
        .runWith(Sink.ignore)
    }
  }
}

trait EmbeddedKafkaSandBoxFixture {

  case class Tweet(user: String, timeStamp: Long, body: String)

  val topicName = "my-topic"

  val tweets = Seq(
    Tweet("jimmy", 1000L, "This is some tweet"),
    Tweet("sam", 1003L, "This is another tweet"),
    Tweet("sam", 1032L, "I like cats #cats4life"),
    Tweet("sam", 1042L, "I like dogs #dogs4life")
  )

}
