package com.mlh.clustering.actor

import akka.actor.{Actor, ActorLogging}

import scala.concurrent.Future
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by pek on 2017/10/20.
  */


class ActorC
  extends Actor
    with ActorLogging {

  def receive: Receive = {
    case "pingC" =>

      log.info("ActorC called.")

      sender() ! f

    case _ =>  log.info("Unsupported message.")
  }

  val f: Future[String] = Future {
    Thread.sleep(100)
    "Success"
  }

}