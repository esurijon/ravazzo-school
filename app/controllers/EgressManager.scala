package controllers

import com.google.inject.ImplementedBy

import javax.inject.Inject
import model.Id
import play.api.cache.CacheApi
import play.api.libs.json.JsError
import play.api.mvc.Action
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import push.GcmPushService
import push.PushService

class EgressManager @Inject() (cache: CacheApi, pushSevice: PushService) extends Controller {

  def noop = Action { Ok }
  def noop2(a: Id) = Action { Ok }

  def registerDevice(nick: String) = Action(BodyParsers.parse.json) { request =>

    request.body.validate[String].fold(
      errors => InternalServerError(JsError.toJson(errors)),
      deviceRegId => {
        cache.set(nick, deviceRegId)
        Ok
      })

  }

}
