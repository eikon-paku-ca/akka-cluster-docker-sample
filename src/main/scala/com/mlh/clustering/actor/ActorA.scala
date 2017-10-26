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


class ActorA
  extends Actor
    with ActorLogging {
  context.system.scheduler.schedule(5 second, 5000 milliseconds, self, "start")
  implicit val timeout = akka.util.Timeout(1000 milliseconds)
  def receive: Receive = {
    case "start" =>

      (1 to 10) foreach{
        id =>
          if (id != 10){
            log.info("======start===== millisecond : {} id : {}", Instant.now().toEpochMilli.toString, id)
            (context.system.actorSelection("user/actorB_%d" format id) ? id).mapTo[String].onComplete {
              case Success(x) => log.info("==========ActorStart=========== result : {}", x)
              case Failure(ex) => {
                log.error("======error===== millisecond : {} id : {} \n {}", Instant.now().toEpochMilli.toString, id,ExceptionUtil.stackTraceString(ex))
              }
            }
            log.info("=======end====== millisecond : {} id : {}", Instant.now().toEpochMilli.toString, id)
          }
      }

    case _ =>  log.info("Unsupported message.")
  }
}