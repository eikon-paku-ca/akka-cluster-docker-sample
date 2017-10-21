package com.mlh.clustering.actor

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorPath, Identify, PoisonPill, Props}
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

  context.system.scheduler.schedule(5 second, 5 second, self, "tick")
  context.system.scheduler.schedule(5 second, 10 second, self, "start")
  implicit val timeout = akka.util.Timeout(100 milliseconds)

  val _id = id

  override def receive: Receive = {
    case "start" =>
      log.info("Current Actors in system:")
      self ! ActorPath.fromString("akka://clustering-cluster/user/")

    case path: ActorPath =>
      log.info("Current Actors in system:")
      context.actorSelection(path / "*") ! Identify(())
    case ActorIdentity(_, Some(ref)) =>
      log.info(ref.toString())
      self ! ref.path

    case "tick" => {
      log.info("tick is running..============================")

//      self ! "start"
//      system.actorSelection(CountActor.path) ? "TEST"
      system.actorSelection(CountActor.path).ask(CountActor.Count(_id)).mapTo[Int].onComplete{
//      (system.actorSelection(CountActor.path) ? "TEST").mapTo[String].onComplete{
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
