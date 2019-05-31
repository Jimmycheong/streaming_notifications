package com.jimmy.code.tutorials

import java.time.Duration
import java.util

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import net.manub.embeddedkafka.ops.ProducerOps
import net.manub.embeddedkafka.{Consumers, EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.FlatSpec

class EmbeddedKafkaSandBoxSpec2 extends FlatSpec with EmbeddedKafka
  with ProducerOps[EmbeddedKafkaConfig] with Consumers with EmbeddedKafkaSandBoxFixture2 {

  "Example" should "do something" in {

    withRunningKafkaOnFoundPort(embeddedKafkaConfig) { implicit embeddedKafkaConfig =>

      data.foreach { tweet =>
        val serializedTweet = tweet.asJson.noSpaces

        println(s"Publishing: $serializedTweet")
        publishToKafka(topicName, serializedTweet)
      }

      implicit val stringDeserializer = new StringDeserializer

      withConsumer { consumer: KafkaConsumer[String, String] =>
        consumer.subscribe(util.Arrays.asList(topicName))

        val duration = Duration.ofMillis(10000)
        val records: ConsumerRecords[String, String] = consumer.poll(duration)

        records.forEach { record =>
          decode[Tweet](record.value) match {
            case Right(value) => println(s"user: ${value.user}, body: ${value.body}, timeStamp: ${value.timeStamp}")
          }
        }
      }
    }
  }

}

trait EmbeddedKafkaSandBoxFixture2 {

  implicit val serializer: StringSerializer = new StringSerializer

  case class Tweet(user: String, timeStamp: Long, body: String)

  val kafkaPort = 12345
  val topicName = "my-topic"
  val embeddedKafkaConfig = EmbeddedKafkaConfig()

  val data = Seq(
    Tweet("jimmy", 1000L , "This is some tweet"),
    Tweet("sam", 1003L,"This is another tweet"),
    Tweet("sam", 1032L,"I like cats #cats4life"),
    Tweet("sam", 1042L,"I like dogs #dogs4life")
  )

  def createConsumerSettings(system: ActorSystem): ConsumerSettings[String, String] = {
    val config = system.settings.config.getConfig("akka.kafka.consumer")

    ConsumerSettings(config, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:6001")
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  }
}
