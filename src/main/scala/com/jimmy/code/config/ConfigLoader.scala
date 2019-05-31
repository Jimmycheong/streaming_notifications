package com.jimmy.code.config

import com.typesafe.config.{Config, ConfigFactory}

trait ConfigLoader {
  val config: Config          = ConfigFactory.load()
  val consumerConfig: Config  = config.getConfig("akka.kafka.consumer")
  val topicName: String       = config.getString("kafka.topicName")
}
