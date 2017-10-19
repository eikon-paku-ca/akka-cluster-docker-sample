package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, PoisonPill}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import com.mlh.clustering._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by pek on 2017/10/20.
  */
class TickActor
  extends Actor
  with ActorLogging{

    override def preStart = self ! "tick"
  system.scheduler.schedule(1 second,1 second,self,"tick")

  var count = 0
    override def receive: Receive = {
      case "tick" => {
        count += 1
        log.info("count.{}", count)
      }

      case "stop"   => {
        log.info("AccountListActor is stop.")
        self ! PoisonPill
      }
    }
}
