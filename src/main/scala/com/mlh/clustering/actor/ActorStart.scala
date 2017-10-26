package com.mlh.clustering.actor

import java.time.Instant

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import com.mlh.clustering.ExceptionUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}
/**
  * Created by pek on 2017/10/20.
  */


class ActorStart
  extends Actor
    with ActorLogging {

  implicit val timeout = akka.util.Timeout(1000 milliseconds)
    context.system.scheduler.schedule(5 second, 500 milliseconds, self, "start")
//  override def preStart = self ! "start"
  def receive: Receive = {
    case "start" =>

      log.info("ActorStart called.")

      (1 to 10) foreach { id =>
        if (id != 10){
          log.info("======start===== millisecond : {} id : {}", Instant.now().toEpochMilli.toString, id)
          (context.system.actorSelection("user/actorA") ? id).mapTo[String].onComplete {
            case Success(x) => log.info("==========ActorStart=========== result : {}", x)
            case Failure(ex) => log.error(ExceptionUtil.stackTraceString(ex))
          }
          log.info("=======end====== millisecond : {} id : {}", Instant.now().toEpochMilli.toString, id)
        }

      }
    case _ =>  log.info("Unsupported message.")
  }
}