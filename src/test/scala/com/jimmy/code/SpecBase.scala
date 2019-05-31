package com.jimmy.code

import akka.kafka.testkit.scaladsl.ScalatestKafkaSpec
import org.scalatest.{FlatSpecLike, Matchers}
import org.scalatest.concurrent.{Eventually, ScalaFutures}

abstract class SpecBase(kafkaPort: Int)
    extends ScalatestKafkaSpec(kafkaPort)
    with FlatSpecLike
    with Matchers
    with ScalaFutures
    with Eventually
