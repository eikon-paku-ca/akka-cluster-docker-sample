package com.mlh.clustering.actor

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorPath, Identify, PoisonPill, Props}
import com.mlh.clustering.ExceptionUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * Created by pek on 2017/10/20.
  */
class EachAccountActor(id: Int) extends Actor with ActorLogging {

  val actor = Some(context.system.scheduler.schedule(20 second, 20 second, self, "tick"))

  override def postStop() = {
    log.info("スケジューラを停止しますすすすすすすすすすすす！！！！")
    actor.foreach(_.cancel())
  }


  val _id = id
  var countLimit = _id * 10000
  implicit val timeout = akka.util.Timeout(1.seconds)
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
    case "ping" => {
      log.info("account id {} get ping" , _id)
      f.onComplete {
        case Success(x) => sender() ! x
        case Failure(ex) =>
          log.error(ExceptionUtil.stackTraceString(ex))
          throw ex
      }
    }
    case "tick" => {
      countLimit -= 1
      log.info("tick is running..============================ id : {} count : {}" , _id, countLimit)
//      self ! "start"
//      system.actorSelection("user/routerActor/workerRouter") ! "user/routerActor/workerRouter"
//      system.actorSelection("user/routerActor") ! CountActor.Count(_id)
//      system.actorSelection("user/routerActor").ask(CountActor.Count(_id)).mapTo[Int].onComplete{
////      (system.actorSelection(CountActor.path) ? "TEST").mapTo[String].onComplete{
//        case Success(count) => log.info("id : {} count : {}", _id, count)
//        case Failure(ex) => log.error(ExceptionUtil.stackTraceString(ex))
//      }


    }

    case "stop" => {
      log.info("AccountListActor is stop.")
      self ! PoisonPill
    }
  }
    val f: Future[String] = Future {
      Thread.sleep(1000)
      "PONG!"
    }

}

object EachAccountActor {
  def props(id: Int): Props = Props(new EachAccountActor(id))
}
