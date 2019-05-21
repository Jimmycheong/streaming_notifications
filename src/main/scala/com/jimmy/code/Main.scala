package com.jimmy.code

import akka.stream._
import akka.stream.scaladsl._
import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

/**
  Read the Akka Streams quickstart guide:
  https://doc.akka.io/docs/akka/2.5/stream/stream-quickstart.html

 */

object Main extends App {

  println("Do stuff")

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)

  source.runForeach(i => println(i))(materializer)

}
