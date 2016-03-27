package controllers

import anorm.SqlParser._
import com.google.inject.ImplementedBy
import anorm._
import javax.inject.Inject
import model.Id
import play.api.cache.CacheApi
import play.api.db.Database
import play.api.libs.json.JsError
import play.api.mvc.Action
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import play.db.NamedDatabase
import push.GcmPushService
import push.PushService
import play.api.libs.json.Json
import model.Departure

class EgressManager @Inject() (cache: CacheApi, pushSevice: PushService, @NamedDatabase("default") db: Database) extends Controller {

  def getAssignedChildren = Action {

    val parentId = "esurijon"
    val departuresByParentQuery = s"""
SELECT 
  parent.id as parent, 
  children.id as child, 
  classroom.dispatcher as dispatcher, 
  classroom.id as classroom, 
  gate.id as gate, 
  gate.gatekeeper as gatekeeper
FROM 
  school.permanent_authorization, 
  school.classroom, 
  school.children, 
  school.shift, 
  school.parent, 
  school.gate
WHERE 
  permanent_authorization.children = children.id AND
  children.classroom = classroom.id AND
  shift.classroom = classroom.id AND
  shift.gate = gate.id AND
  parent.id = '$parentId';
"""
    val departureParser = Macro.namedParser[Departure]
    val departures = db.withConnection { implicit c =>
      val result = SQL(departuresByParentQuery).as(departureParser.*)
      result
    }
    Ok(Json.toJson(departures))
  }

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
