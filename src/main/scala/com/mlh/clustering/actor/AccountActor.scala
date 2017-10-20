package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, PoisonPill}
import com.mlh.clustering.actor.AccountActor.{End, Start}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
  * Created by pek on 2017/10/20.
  */
class AccountActor
  extends Actor
  with ActorLogging {

  implicit val timeout = akka.util.Timeout(100 milliseconds)

  override def preStart = self ! Start

  def receive: Receive = {
    case Start    => {
      log.info("AccountActor is start. ")
      (1 to 10) foreach (i => context.actorOf(EachAccountActor.props(i), name = s"$AccountActor.baseName$i"))
    }
    case End => {
      log.info("AccountActor is end. ")
      self ! PoisonPill
    }
  }
}

object AccountActor {
  case object Start
  case object End
  val baseName = "EachAccountActor>>>>>>>>>>>>>>>>>>>>>"
  val name = "accountActor"
}