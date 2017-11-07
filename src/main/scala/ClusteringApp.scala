package com.mlh.clustering

import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.ClusterDomainEvent
import com.mlh.clustering.actor.DelayMessageConsumerActor

object ClusteringApp extends App {

   val clusterListener = system.actorOf(Props[ClusterListener], name = "clusterListener")
  Cluster(system).subscribe(clusterListener, classOf[ClusterDomainEvent])
  // router起動
  system.actorOf(Props(classOf[DelayMessageConsumerActor], clusterListener), name = DelayMessageConsumerActor.name)

//#   AccountActorはsingleton
//  Cluster SingletonでAccountListActorを起動する
//  val singletonProps = ClusterSingletonManager.props(
//    singletonProps = Props[AccountActor],
//    terminationMessage = PoisonPill,
//    settings = ClusterSingletonManagerSettings(system)
//  )
//  system.actorOf(singletonProps, AccountActor.name)
//  system.actorOf(Props[AccountActor], name = AccountActor.name)
//  //#   CountActorはsingleton
//  val singletonProps1 = ClusterSingletonManager.props(
//    singletonProps = Props[CountActor],
//    terminationMessage = PoisonPill,
//    settings = ClusterSingletonManagerSettings(system)
//  )
//  val countActor = system.actorOf(singletonProps1, CountActor.name)

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
