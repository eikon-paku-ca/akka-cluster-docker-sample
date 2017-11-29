package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, Cancellable}
import com.mlh.clustering.system

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
  * Created by pek on 2017/10/20.
  */
//class DelayMessageConsumerActor(private val clusterListener: ActorRef)
//  extends Actor
//    with ActorLogging {
//  var poolList: scala.collection.mutable.HashMap[Int, ActorRef] = scala.collection.mutable.HashMap.empty[Int, ActorRef]
//
//  private lazy val workerRouterPool =
//    context.actorOf(FromConfig.props(Props(classOf[DelayMessageConsumerWorkerActor])), name = DelayMessageConsumerWorkerActor.name)
//
//
//  system.scheduler.schedule(10 second, 10 second, self, "TICK")
////  override def preStart(): Unit = self ! (1 to 25).toList
//  def receive: Receive = {
//    case countList: List[Int] =>
////      Thread.sleep(30000)
//      countList foreach {
//        cnt =>
//          Thread.sleep(1000)
//          workerRouterPool.tell(cnt + "_" + 1, clusterListener)
//      }
////      system.scheduler.schedule(10 second, 10 second, self, "TICK")
//
//    case "TICK" =>
//      log.info("TACK")
//  }
//
//}
//
//object DelayMessageConsumerActor {
//  val name = "delayMessageConsumerActor"
//  val path = s"user/$name/singleton"
//  def getActorSelection: ActorSelection = {
//    system.actorSelection(path)
//  }
//
//}

class DelayMessageConsumerWorkerActor
  extends Actor
    with ActorLogging {

  override def preStart(): Unit = log.info("workerRouter run.")

  def receive: Receive = {
    case cnt:String =>
      send(cnt)
    case _ =>  log.info("Unsupported message.")
  }

  def send(cnt: String) = {
    log.info("cnt = {}", cnt)
    if (cnt.split("_")(0) == "1")
      (1 to 20) foreach{
        id =>
          CallApiHelper.getActorSelection(cnt.split("_")(0).toInt) ! cnt + " index_" + id
      }
    else
      CallApiHelper.getActorSelection(cnt.split("_")(0).toInt) ! cnt

    scheduleDelayConsumer(cnt)
  }
  val f: Future[String] = Future {
    Thread.sleep(100)
    "Success"
  }
  def scheduleDelayConsumer(cnt: String): Cancellable = {
    system.scheduler.scheduleOnce(10 second) {
      val sp = cnt.split("_")
      self ! sp(0) + "_" + (sp(1).toInt + 1)
    }
  }

}

object DelayMessageConsumerWorkerActor {
  val name = "workerRouter"
  val path = s"user/$name"
}