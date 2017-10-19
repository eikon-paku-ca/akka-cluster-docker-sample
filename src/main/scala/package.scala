package com.mlh

import akka.actor.ActorSystem

package object clustering {
  import com.mlh.clustering.ClusteringConfig._

  implicit val system = ActorSystem(clusterName)

}


