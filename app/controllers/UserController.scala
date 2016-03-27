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
import model.Person

class UserController @Inject() (@NamedDatabase("default") db: Database) extends Controller {

  def getPerson(personId: Id) = Action {

    val personByIdQuery = s"""
SELECT 
  id, firstName, lastName, nickName, role
FROM 
  school.person
WHERE 
  id = '$personId';
"""
    val personParser = Macro.namedParser[Person] 
    val person = db.withConnection { implicit c =>
      SQL(personByIdQuery).as(personParser.single)
    }
    Ok(Json.toJson(person))
  }
}
