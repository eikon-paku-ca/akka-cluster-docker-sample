package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import com.mlh.clustering.system

import scala.language.postfixOps

/**
  * Created by pek on 2017/10/20.
  */
class CallApiActor(id: Int) extends Actor with ActorLogging {

//  val actor = Some(context.system.scheduler.schedule(20 second, 10 second, self, "tick"))

//  override def postStop() = {
//    log.info("スケジューラを停止しますすすすすすすすすすすす！！！！")
//    actor.foreach(_.cancel())
//  }


  override def preStart() =
    log.info("CallApiActor is start. accountId : {} ", id.toString)

  override def receive: Receive = run()

  private def run(): Receive = {
    case m: String => {
      log.info("CallApiActor : {} {}", m, self.path.toStringWithoutAddress)
    }

    case _ =>
      log.warning("CallApiActor: unsupported message.")

  }

}

object CallApiActor {
  def props(id: Int): Props = Props(new CallApiActor(id))
}
object CallApiHelper {
  def props(id: Int): Props = Props(new CallApiActor(id))
  private lazy val CALL_API_ACTOR_NAME_PREFIX = "apiRouter_%d"

  def generateActorName(accountId: Int): String = {
    CALL_API_ACTOR_NAME_PREFIX format accountId
  }

  def getActorPath(accountId: Int): String = {
    "%s/singleton/%s" format (AccountListActor.path, generateActorName(accountId))
//    "user/%s" format generateActorName(accountId)
  }

  def getActorSelection(accountId: Int): ActorSelection = {
//    system.actorSelection(CallApiHelper.getActorPath(accountId))
    system.actorSelection(s"user/%s" format generateActorName(accountId))
  }
}