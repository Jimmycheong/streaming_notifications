package com.jimmy.code.tutorials

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

  val factorials: Source[BigInt, NotUsed] =
    source.scan(BigInt(1))((acc, next) => acc * next)

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s => ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  val result: Future[IOResult] = factorials.map(_.toString).runWith(lineSink("factorials.txt"))

  implicit val ec = system.dispatcher
  result.onComplete(_ => system.terminate())
}
