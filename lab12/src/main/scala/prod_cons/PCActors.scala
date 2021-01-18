package twactors

import java.lang.Math
import java.util.Random

import akka.actor.Actor
import akka.actor.Stash
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.Logging
import akka.event.LoggingReceive
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.Await

// Assignment:
// - implement solution to the producers/consumers problem
//   using the actor model / Akka
// - test the correctness of the created solution for multiple
//   producers and consumers
// Hint: use akka.actor.Stash

// object PC contains messages for all actors -- add new if you need them
object PC {
  case class Init
  case class Put(x: Long)
  case class Get
  case class ProduceDone
  case class ConsumeDone(x: Long)
}

class Producer(name: String, buf: ActorRef) extends Actor {
  import PC._

  def receive = ???

}

class Consumer(name: String, buf: ActorRef) extends Actor {
  import PC._

  def receive = ???

}


class Buffer(n: Int) extends Actor {
  import PC._

  private val buf = new Array[Long](n)
  private var count = 0

  def receive = LoggingReceive {
    case Put(x) if (count < n) => ???
    case Get if (count > 0) => ???
    case _ => ???
  }
}


object ProdConsMain extends App {
  import PC._

  val system = ActorSystem("ProdKons")

  // TODO: create Consumer actors. Use "p ! Init" to kick them off

  // TODO: create Producer actors. Use "p ! Init" to kick them off


  Await.result(system.whenTerminated, Duration.Inf)
}



