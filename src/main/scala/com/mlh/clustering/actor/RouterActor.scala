package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.pattern.ask
import akka.routing.FromConfig
import com.mlh.clustering._
import com.mlh.clustering.actor.CountActor.{Count, End, Start}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
  * Created by pek on 2017/10/20.
  */
class RouterActor(private val clusterListener: ActorRef)
  extends Actor
  with ActorLogging{

  implicit val timeout = akka.util.Timeout(1.seconds)
  private lazy val routerPool =
    system.actorOf(FromConfig.props(Props[CountActor]), name = "workerRouter")

  override def preStart = self ! Start

  def receive: Receive = {
    case Start    => {
      log.info("RouterActor is start. ")
      routerPool.tell(Start, clusterListener)
    }

    //case msg :Any  => sender() ! routerPool.ask(msg, clusterListener).mapTo[Any]
    case Count  => sender() ! routerPool.ask(Count, clusterListener).mapTo[Int]

    case End => {
      log.info("RouterActor is end. ")
      routerPool ! End
    }
  }

}

object RouterActor {
  val name  = "routerActor"
  val path = s"user/$name"
}

class CountActor
  extends Actor
    with ActorLogging{

  implicit val timeout = akka.util.Timeout(1.seconds)
  override def preStart = self ! Start
  val countHashMap = scala.collection.mutable.HashMap.empty[Int, Int]
  def receive: Receive = {
    case Start    => {
      log.info("CountActor is start. ")
      (1 to 10) foreach (
        i =>
          countHashMap += i -> (1 * 10000)
      )
    }

    case Count(id) => {
      countHashMap(id) -= 1
      sender() ! countHashMap(id)
    }

    case End => {
      log.info("RequestCountRouterActor is end. ")
      self ! PoisonPill
    }
  }

}
object CountActor{
  case object Start
  case class Count(id: Int)
  case object End
  val name = "countActor"
  val path = s"user/$name"
}

