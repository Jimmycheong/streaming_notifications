package com.jimmy.code.tutorials

import akka.stream._
import akka.stream.scaladsl._
import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

class ThrottlingExample extends App {
    println("Do stuff")

    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()

    val source: Source[Int, NotUsed] = Source(1 to 100)

    val factorials: Source[BigInt, NotUsed] =
      source.scan(BigInt(1))((acc, next) => acc * next)

    val result = factorials
      .zipWith(Source(0 to 100))((num, idx) => s"$idx! = $num")
      .throttle(1, 1.second)
      .runForeach(println)

    implicit val ec = system.dispatcher
    result.onComplete(_ => system.terminate())
}
