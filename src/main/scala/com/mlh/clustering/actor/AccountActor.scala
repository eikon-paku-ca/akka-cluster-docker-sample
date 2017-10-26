package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, PoisonPill}
import com.mlh.clustering._
import com.mlh.clustering.actor.AccountActor.{End, Start}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import akka.pattern.ask

import scala.concurrent.Future

/**
  * Created by pek on 2017/10/20.
  */
class AccountActor
  extends Actor
  with ActorLogging {

  implicit val timeout = akka.util.Timeout(1000 milliseconds)
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
      val result = getPing()
      log.info("result : {}", result)
    case End => {
      log.info("AccountActor is end. ")
      self ! PoisonPill
    }
  }
  def getPing(): Future[String] = {
    (system.actorSelection("user/accountActor/eachAccountActor1") ? "ping").mapTo[String]
  }
}

object AccountActor {
  case object Start
  case object End
  val baseName = "eachAccountActor"
  val name = "accountActor"
}