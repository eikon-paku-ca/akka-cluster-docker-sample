package com.mlh.clustering.actor

object ActorHelper {
  val START = "start"
  val STOP  = "stop"
  val FETCH = "fetch"
  case class PushMessage(accountId: Int,
                         encryptedUserId: String,
                         layer: Option[String],
                         channelAccessToken: String,
                         message: String,
                         sendType: String,
                         id: Option[Int])
  val SECONDS_OF_MINUTES = 60
}
