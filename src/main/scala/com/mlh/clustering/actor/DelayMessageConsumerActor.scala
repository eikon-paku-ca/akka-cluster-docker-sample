package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Cancellable, Props}
import akka.routing.FromConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import com.mlh.clustering.system
import scala.concurrent.duration.DurationInt

/**
  * Created by pek on 2017/10/20.
  */
class DelayMessageConsumerActor(private val clusterListener: ActorRef)
  extends Actor
    with ActorLogging {
  var poolList: scala.collection.mutable.HashMap[Int, ActorRef] = scala.collection.mutable.HashMap.empty[Int, ActorRef]

  private lazy val workerRouterPool =
    context.actorOf(FromConfig.props(Props(classOf[DelayMessageConsumerWorkerActor])), name = DelayMessageConsumerWorkerActor.name)

  override def preStart(): Unit = self ! (1 to 10).toList

  def receive: Receive = {
    case countList: List[Int] =>
      countList foreach (cnt => workerRouterPool.tell(cnt, clusterListener))
  }

}

object DelayMessageConsumerActor {
  val name = "delayMessageConsumerActor"
  val path = s"user/$name"
  def getActorSelection: ActorSelection = {
    system.actorSelection(path)
  }

}

class DelayMessageConsumerWorkerActor
  extends Actor
    with ActorLogging {

  def receive: Receive = {
    case cnt:Int =>
      run(cnt)
    case _ =>  log.info("Unsupported message.")
  }

  def run(cnt: Int) = {
    log.info("cnt = {}", cnt)
    scheduleDelayConsumer(cnt)
  }
  val f: Future[String] = Future {
    Thread.sleep(100)
    "Success"
  }
  def scheduleDelayConsumer(cnt: Int): Cancellable = {
    system.scheduler.scheduleOnce(10 second) {
      self ! cnt + 1
    }
  }

}

object DelayMessageConsumerWorkerActor {
  val name = "workerRouter"
  val path = s"user/$name"
}