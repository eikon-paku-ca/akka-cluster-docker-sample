package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Success
/**
  * Created by pek on 2017/10/20.
  */


class ActorB
  extends Actor
    with ActorLogging {

  def receive: Receive = {
    case id: Int =>

      log.info("ActorB called. id {}", id)
      val f: Future[String] = Future {
//        Thread.sleep(30)
        "Success id %d" format id
      }
      Await.ready(f, Duration.Inf)
      f.value.get match {
        case Success(x) => sender() ! x
      }
    case _ =>  log.info("Unsupported message.")
  }


}