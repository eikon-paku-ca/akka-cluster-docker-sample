package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.routing.FromConfig
import com.mlh.clustering._
import com.mlh.clustering.actor.AccountListActor.{End, Start}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
  * Created by pek on 2017/10/20.
  */
class AccountListActor
  extends Actor
  with ActorLogging {

  implicit val timeout = akka.util.Timeout(1000 milliseconds)
//  context.system.scheduler.schedule(5 second, 5 second, self, "ping")

  override def preStart = self ! Start

  def receive: Receive = {
    case Start    => {
      log.info("AccountActor is start. ")
      (1 to 10) foreach { i =>
        context.actorOf(FromConfig.props(CallApiActor.props(i)), name = CallApiHelper.generateActorName(i))
      }
    }
    case "ping" =>
      val result = getPing()
      log.info("result : {}", result)

    case End => {
      log.info("AccountActor is end. ")
      context stop self
    }
  }
  def getPing(): Future[String] = {
    (system.actorSelection("user/accountActor/eachAccountActor1") ? "ping").mapTo[String]
  }
}

object AccountListActor {
  case object Start
  case object End
  val name = "accountList"
  val path = s"user/$name"
}

