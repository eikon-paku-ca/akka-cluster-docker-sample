package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.pattern.ask
import com.mlh.clustering._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * Created by pek on 2017/10/20.
  */
class EachAccountActor(id: Int) extends Actor with ActorLogging {

  override def preStart = self ! "tick"

  context.system.scheduler.schedule(1 second, 1 second, self, "tick")

  implicit val timeout = akka.util.Timeout(100 milliseconds)

  val _id = id

  override def receive: Receive = {
    case "tick" => {
      (system.actorSelection(RouterActor.path) ? CountActor.Count(1)).mapTo[Int].onComplete{
        case Success(count) => log.info("id : {} count : {}", _id, count)
        case Failure(ex) => log.error(ExceptionUtil.stackTraceString(ex))
      }


    }

    case "stop" => {
      log.info("AccountListActor is stop.")
      self ! PoisonPill
    }
  }

}

object EachAccountActor {
  def props(id: Int): Props = Props(new EachAccountActor(id))
}
