package com.aulatec.push

import scala.Left
import scala.Right
import scala.concurrent.Future

import com.google.inject.ImplementedBy

import javax.inject.Inject
import javax.inject.Singleton
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.json.Writes
import play.api.libs.ws.WSClient

@ImplementedBy(classOf[GcmPushService])
abstract class PushService {
  def sendMessage[T](message: PushMessage[T])(implicit wrt: Writes[Data[T]]): Future[Either[String, Unit]]
}

@Singleton
class GcmPushService @Inject() (ws: WSClient, conf: Configuration) extends PushService {

  val endpoint = conf.getString("gcm.endpoint").get
  val apiKey = conf.getString("gcm.apiKey").get

  override def sendMessage[T](message: PushMessage[T])(implicit wrt: Writes[Data[T]]): Future[Either[String, Unit]] = {

    val pushRequest = ws
      .url(endpoint)
      .withHeaders(
        "Content-Type" -> "application/json",
        "Authorization" -> s"key=$apiKey")

    pushRequest.post(Json.toJson(message)).map { pushResponse =>
      if (pushResponse.status >= 300) {
        Left(pushResponse.statusText)
      } else {
        Right(())
      }
    }

  }
}