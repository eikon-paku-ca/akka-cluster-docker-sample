package com.mlh.clustering

import akka.actor.{PoisonPill, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.ClusterDomainEvent
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.routing.FromConfig
import com.mlh.clustering.actor.{AccountListActor, CallApiActor, CallApiHelper}

object ClusteringApp extends App {

   val clusterListener = system.actorOf(Props[ClusterListener], name = "clusterListener")

  Cluster(system).subscribe(clusterListener, classOf[ClusterDomainEvent])
  (1 to 6) foreach { i =>
    system.actorOf(FromConfig.props(CallApiActor.props(i)), name = CallApiHelper.generateActorName(i))
  }

//  val isRouter = Some(System.getenv("is_leader")).getOrElse("false")

  // router起動
//  if (isRouter == "is_router")
//    system.actorOf(Props(classOf[DelayMessageConsumerActor], clusterListener), name = DelayMessageConsumerActor.name)

//#   AccountActorはsingleton
//  Cluster SingletonでAccountListActorを起動する
//  val singletonProps = ClusterSingletonManager.props(
//    singletonProps = Props(classOf[DelayMessageConsumerActor], clusterListener),
//    terminationMessage = PoisonPill,
//    settings = ClusterSingletonManagerSettings(system)
//  )
//  system.actorOf(singletonProps, DelayMessageConsumerActor.name)
//
  val singletonProps_accountList = ClusterSingletonManager.props(
    singletonProps = Props(classOf[AccountListActor], clusterListener),
    terminationMessage = PoisonPill,
    settings = ClusterSingletonManagerSettings(system)
  )
  system.actorOf(singletonProps_accountList, AccountListActor.name)
//  system.actorOf(Props(classOf[AccountListActor], clusterListener), name = AccountListActor.name)
  // アカウントごとのActorはルートパスから起動する。子Actorにしてしまうとシングルトーンから実行されるとき１ノードでしか動かない
//  (1 to 10) foreach { i =>
//    Thread.sleep(5000)
//    system.actorOf(EachAccountActor.props(i), name = "%s%d" format (AccountActor.baseName, i))
//  }


  // Ask time out test
//  system.actorOf(Props[ActorA], name = "actorA")
//  (1 to 10).foreach{ m =>
//    system.actorOf(Props[ActorB], name = "actorB_%d" format m)
//  }
//  system.actorOf(Props[ActorStart], name = "actorStart")

  // shutdown
  sys.addShutdownHook(system.terminate())
}
