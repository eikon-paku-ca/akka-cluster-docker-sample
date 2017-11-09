package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.routing.FromConfig
import com.mlh.clustering.actor.ActorHelper._
import com.mlh.clustering.system

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
  * Created by pek on 2017/10/20.
  */
class AccountListActor(private val clusterListener: ActorRef)
  extends Actor
  with ActorLogging {

  implicit val timeout = akka.util.Timeout(1000 milliseconds)
//  context.system.scheduler.schedule(5 second, 5 second, self, "ping")

  override def preStart = self ! START

  val idList = (1 to 6).toList
  def receive: Receive = {
    case START    => {
      log.info("AccountActor is start. ")

      // CallApiActor起動
      idList foreach { i =>
        context.actorOf(FromConfig.props(CallApiActor.props(i)), name = CallApiHelper.generateActorName(i))
//        context.actorOf(CallApiActor.props(i), name = CallApiHelper.generateActorName(i))
      }


      val isRouter = Some(System.getenv("is_leader")).getOrElse("false")
      if (isRouter == "is_router") {

      }
      // DelayWorkerRouterActor起動Singleton
      val singletonProps = ClusterSingletonManager.props(
        singletonProps = FromConfig.props(Props(classOf[DelayMessageConsumerWorkerActor])),
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(system)
      )
      val workerRouter = context.actorOf(singletonProps, DelayMessageConsumerWorkerActor.name)
//      val workerRouter = context.actorOf(FromConfig.props(Props(classOf[DelayMessageConsumerWorkerActor])), name = DelayMessageConsumerWorkerActor.name)
      Thread.sleep(4000)
      idList foreach { i =>
        // 初期メッセージ
        Thread.sleep(1000)
        workerRouter.tell(i + "_" + 1,  clusterListener)
      }
    }
    case STOP => {
      log.info("AccountActor is end. ")
      context stop self
    }
  }
}

object AccountListActor {
  val name = "accountList"
  val path = s"user/$name"
}

