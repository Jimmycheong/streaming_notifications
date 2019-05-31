package com.jimmy.code

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import com.jimmy.code.config.ConfigLoader
import com.jimmy.code.tutorials.Tweet
import com.jimmy.code.wiring.TwitterConsumerWiring
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.{ExecutionContextExecutor, Future}

object TwitterConsumer extends TwitterConsumerWiring with ConfigLoader {

  def main(args: Array[String]): Unit = {

    val consumerSettings = ConsumerSettings(consumerConfig,
                                            new StringDeserializer,
                                            new StringDeserializer)

    implicit val system: ActorSystem = ActorSystem("twitter-consumer")
    implicit val mat: Materializer = ActorMaterializer()
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    startConsuming(consumerSettings, topicName)
  }

  def startConsuming[K, V](consumerSettings: ConsumerSettings[K, V],
                           topicName: String)(
      implicit system: ActorSystem,
      mat: Materializer,
      ec: ExecutionContextExecutor
  ): Future[Done] = {

    val consumerSettings = ConsumerSettings(consumerConfig,
                                            new StringDeserializer,
                                            new StringDeserializer)
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topicName))
      .map { record: ConsumerRecord[String, String] =>
        println(s"Record: ${record.value}")
      }
      .runWith(Sink.ignore)
  }

  def writeTweetToFile(tweet: Tweet): Unit = {}

}
