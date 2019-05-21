package com.jimmy.code

import akka.stream._
import akka.stream.scaladsl._
import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

trait ReactiveTweetExampleFixture {
  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] =
      body
        .split(" ")
        .collect {
          case t if t.startsWith("#") => Hashtag(t.replaceAll("[^#\\w]", ""))
        }
        .toSet
  }

  val akkaTag = Hashtag("#akka")
}

object ReactiveTweetExample extends App with ReactiveTweetExampleFixture {

  import ReactiveTweetExample._

  implicit val actorSystem = ActorSystem("reactive0tweets")
  implicit val materializer = ActorMaterializer()

  val tweets: Source[Tweet, NotUsed] = Source(List(
    Tweet(Author("jim"), 1000, "#okay there"),
    Tweet(Author("david"), 1000, "#okay world"),
    Tweet(Author("simon"), 1000, "#hi there"),
    Tweet(Author("jason"), 1000, "#akka is fun"),
    Tweet(Author("tom"), 1000, "#akka is here")
  ))

  val authors: Source[Author, NotUsed] =
    tweets.filter(_.hashtags.contains(akkaTag)).map(_.author)

  val result = authors.runWith(Sink.foreach(println))
  // OR authors.runForeach(println)

  implicit val ec = actorSystem.dispatcher
  result.onComplete(_ => actorSystem.terminate())
}
