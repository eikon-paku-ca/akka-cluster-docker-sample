package com.mlh.clustering

import akka.actor._
import com.mlh.clustering.actor.TickActor

object ClusteringApp extends App {

   val clusterListener = system.actorOf(Props[ClusterListener], name = "clusterListener")
  system.actorOf(Props[TickActor], name = "tickActor")
  sys.addShutdownHook(system.shutdown())
}
