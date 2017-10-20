package com.mlh.clustering

import akka.actor.{PoisonPill, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.ClusterDomainEvent
import com.mlh.clustering.actor.{AccountActor, RouterActor}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}

object ClusteringApp extends App {

   val clusterListener = system.actorOf(Props[ClusterListener], name = "clusterListener")
  Cluster(system).subscribe(clusterListener, classOf[ClusterDomainEvent])
  // router起動
  system.actorOf(Props(classOf[RouterActor], clusterListener), name = RouterActor.name)

//#   AccountActorはsingleton
//  Cluster SingletonでAccountListActorを起動する
  val singletonProps = ClusterSingletonManager.props(
    singletonProps = Props[AccountActor],
    terminationMessage = PoisonPill,
    settings = ClusterSingletonManagerSettings(system)
  )
  system.actorOf(singletonProps, AccountActor.name)

  sys.addShutdownHook(system.terminate())
}
