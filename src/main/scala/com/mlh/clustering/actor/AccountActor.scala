package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, PoisonPill}
import com.mlh.clustering.actor.AccountActor.{End, Start}
import com.mlh.clustering._
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by pek on 2017/10/20.
  */
class AccountActor
  extends Actor
  with ActorLogging {

  implicit val timeout = akka.util.Timeout(100 milliseconds)
  context.system.scheduler.schedule(5 second, 5 second, self, "ping")
  override def preStart = self ! Start

  def receive: Receive = {
    case Start    => {
      log.info("AccountActor is start. ")
      (1 to 10) foreach { i =>
        Thread.sleep(5000)
        context.actorOf(EachAccountActor.props(i), name = "%s%d" format(AccountActor.baseName, i))
      }
    }
    case "ping" =>
      system.actorSelection("user/accountActor/eachAccountActor1") ! "ping"
    case End => {
      log.info("AccountActor is end. ")
      self ! PoisonPill
    }
  }
}

object AccountActor {
  case object Start
  case object End
  val baseName = "eachAccountActor"
  val name = "accountActor"
}