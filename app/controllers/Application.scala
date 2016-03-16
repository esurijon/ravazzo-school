package controllers

import scala.concurrent.Future
import javax.inject.Inject
import model.ChatInboundMessage
import model.ChatOutboundMessage
import model.Notification
import model.PushMessage
import play.api.cache.CacheApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.mvc.Action
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import play.api.mvc.Result

class Application @Inject() (cache: CacheApi, ws: WSClient) extends Controller {

  val apiKey = ""

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def registerDevice(nick: String) = Action(BodyParsers.parse.json) { request =>

    request.body.validate[String].fold(
      errors => InternalServerError(JsError.toJson(errors)),
      deviceRegId => {
        cache.set(nick, deviceRegId)
        Ok
      })

  }

  def sendMessage(nick: String) = Action.async(BodyParsers.parse.json) { request =>

    val serrverReceiveTime = System.currentTimeMillis()

    request.body.validate[ChatInboundMessage].fold(
      errors => Future.successful(InternalServerError(JsError.toJson(errors))),
      chatMessage => {

        val s = NotFound(s"No device registered for $nick")
        
        cache.get[String](nick).fold {
          Future.successful(NotFound(s"No device registered for $nick"))
        } { deviceRegId =>

          val pushRequest: WSRequest = ws
            .url("url")
            .withHeaders(
              "Content-Type" -> "application/json",
              "Authorization" -> s"key=$apiKey")

          val notification = Notification(
            "${chatMessage.from}: ${chatMessage.message}".take(15),
            "",
            None)
          val data = ChatOutboundMessage(
            chatMessage.from,
            chatMessage.message,
            chatMessage.clientSentTime,
            serrverReceiveTime,
            System.currentTimeMillis())

          val pushMessage = PushMessage(deviceRegId, Some(notification), Some(Json.toJson(data)))

          pushRequest.post(Json.toJson(pushMessage)).map { pushResponse =>
            new Status(pushResponse.status)(pushResponse.statusText)
          }

        }

      })

  }

}
