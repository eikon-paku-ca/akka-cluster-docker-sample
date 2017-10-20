package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, PoisonPill}
import com.mlh.clustering._
import com.mlh.clustering.actor.AccountActor.{End, Start}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
  * Created by pek on 2017/10/20.
  */
class AccountActor
  extends Actor
  with ActorLogging {

  implicit val timeout = akka.util.Timeout(1.seconds)

  override def preStart = self ! Start

  def receive: Receive = {
    case Start    => {
      log.info("RouterActor is start. ")
      (1 to 10) foreach (i => system.actorOf(EachAccountActor.props(i), name = s"$AccountActor.baseName$i"))
    }
    case End => {
      log.info("RouterActor is end. ")
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