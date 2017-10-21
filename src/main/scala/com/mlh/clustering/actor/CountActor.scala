package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.routing.FromConfig
import com.mlh.clustering._
import com.mlh.clustering.actor.CountActor.{Count, End, Start}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps
import scala.util.{Failure, Success}
import akka.pattern.ask

/**
  * Created by pek on 2017/10/20.
  */


class CountActor
  extends Actor
    with ActorLogging{

  override def preStart = self ! Start
  val countHashMap = scala.collection.mutable.HashMap.empty[Int, Int]
  system.scheduler.schedule(5 second , 5 second ,self, "tick")
  def receive: Receive = {
    case "tick" =>

      log.info("============HashMap==========", countHashMap.toList.mkString(","))
      val s = self.path
      log.info(s.name)
      log.info(s.toString)
    case Start    => {
      log.info("CountActor is start. ")
      (1 to 10) map (
        i =>
          countHashMap += (i -> (i * 10000))
      )
    }

    case x:Count => {
      countHashMap(x.id) -= 1
      log.info("CountActor Count down ================= ")
      sender() ! countHashMap(x.id)
    }

    case End => {
      log.info("RequestCountRouterActor is end. ")
      self ! PoisonPill
    }
    case x => {
      println("=============--------================%s" format x.toString)
      log.info("nothing...")
      //      log.info("-----------================= ", sender.path)
      sender() ! 900
    }
  }

}


object CountActor{
  case object Start
  case class Count(id: Int)
  case object End
  val name = "countRouter"
  val path = s"user/$name/singleton"
}





































class RouterActor(private val clusterListener: ActorRef)
  extends Actor
    with ActorLogging {

  implicit val timeout = akka.util.Timeout(100 milliseconds)
  val singletonProps = ClusterSingletonManager.props(
    singletonProps = Props[CountActor],
    terminationMessage = PoisonPill,
    settings = ClusterSingletonManagerSettings(system)
  )
  private lazy val routerPool =
    system.actorOf(FromConfig.props(singletonProps), name = "workerRouter")

  def receive: Receive = {
    //case msg :Any  => sender() ! routerPool.ask(msg, clusterListener).mapTo[Any]
    case x:Count  => {
      log.info("===============RouterActor is Count. {}", x.id)
      //      routerPool ! Count(i)
      //      sender() ! 1
      val f: Future[Int] = routerPool.ask(x)(timeout, clusterListener).mapTo[Int]
      Await.ready(f, Duration.Inf)
      f.value.get match {
        case Success(num) => sender() ! num
        case Failure(t) => log.error("=============================================Fail: " + t.getMessage())
      }
    }
    case x => {
      log.info("===============RouteCountActor Called...")
      println("===============Message==============" + x.toString)
      val f: Future[Any] = routerPool.ask(Count(1))(timeout, clusterListener).mapTo[Any]
      Await.ready(f, Duration.Inf)
      f.value.get match {
        case Success(any) => sender() ! any
        case Failure(t) => log.error("=============================================Fail: " + t.getMessage())
      }
    }
    case "TEST" => {
      log.info("TEST is RUNNING......................................................................")
      sender() ! "||||||||||||||||||||||||||||||||||||||||"
    }
    case Start    => {
      log.info("RouterActor is start. ")
      routerPool.tell(Start, clusterListener)
    }

    case End => {
      log.info("RouterActor is end. ")
      routerPool ! End
    }
    case _ => log.error("unsupported message.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
  }

}

object RouterActor {
  val name  = "routerActor"
  val path = s"user/$name"
}
