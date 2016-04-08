package controllers

import scala.concurrent.Future

import com.google.inject.ImplementedBy

import javax.inject.Inject
import play.api.cache.CacheApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import com.aulatec.push.PushService

class Application @Inject() (cache: CacheApi, pushSevice: PushService) extends Controller {

  def registerDevice(nick: String) = Action(BodyParsers.parse.json) { request =>

    request.body.validate[String].fold(
      errors => InternalServerError(JsError.toJson(errors)),
      deviceRegId => {
        cache.set(nick, deviceRegId)
        Ok
      })

  }

//  def sendMessage(nick: String) = Action.async(BodyParsers.parse.json) { request =>
//
//    val serverReceiveTime = System.currentTimeMillis()
//
//    request.body.validate[ChatInboundMessage].fold(
//      errors => Future.successful(InternalServerError(JsError.toJson(errors))),
//      chatMessage => {
//
//        cache.get[String](nick).fold {
//          Future.successful(NotFound(s"No device registered for $nick"))
//        } { deviceRegId =>
//
//          val notification = Notification(
//            "${chatMessage.from}: ${chatMessage.message}".take(15),
//            "",
//            None)
//          val data = ChatOutboundMessage(
//            chatMessage.sender,
//            chatMessage.message,
//            chatMessage.clientSentTime,
//            serverReceiveTime,
//            System.currentTimeMillis())
//
//          val pushMessage = PushMessage(deviceRegId, Some(notification), Some(Json.toJson(data)))
//
//          pushSevice.sendMessage(pushMessage) map { result =>
//            result.fold(BadGateway(_), _ => Ok)
//          }
//
//        }
//
//      })
//
//  }

}
