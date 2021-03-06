package com.aulatec.push

import scala.Left
import scala.Right
import scala.concurrent.Future

import org.slf4j.LoggerFactory

import javax.inject.Inject
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.json.Writes
import play.api.libs.ws.WSClient
import javax.inject.Singleton

@Singleton
class GcmPushService @Inject() (ws: WSClient, conf: Configuration) extends PushService {

  private val logger = LoggerFactory.getLogger(classOf[GcmPushService])

  private val endpoint = conf.getString("gcm.endpoint").get
  private val apiKey = conf.getString("gcm.apiKey").get

  override def sendMessage[T](message: PushMessage[T])(implicit wrt: Writes[Data[T]]): Future[Either[String, Unit]] = {

    val pushRequest = ws
      .url(endpoint)
      .withHeaders(
        "Content-Type" -> "application/json",
        "Authorization" -> s"key=$apiKey")

    pushRequest.post(Json.toJson(message)).map { pushResponse =>
      if (pushResponse.status >= 300) {
        logger.error(s"Google Cloud Message Request: ${Json.toJson(message)}")
        logger.error(s"Google Cloud Message Response: ${pushResponse.statusText}")
        Left(pushResponse.statusText)
      } else {
        Json.parse(pushResponse.body).validate[GcmResponse].fold[Either[String, Unit]](
          _ => {
            logger.error(s"Google Cloud Message Request: ${Json.toJson(message)}")
            logger.error(s"Google Cloud Message Response: ${pushResponse.body}")
            Left(pushResponse.body)
          },
          response => {
            if (response.failure == 0) {
              Right(())
            } else {
              logger.error(s"Google Cloud Message Request: ${Json.toJson(message)}")
              logger.error(s"Google Cloud Message Response: ${pushResponse.body}")
              Left(pushResponse.body)
            }
          })
      }
    }

  }
}
