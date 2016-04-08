package controllers

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalTime

import com.google.inject.ImplementedBy

import anorm._
import anorm.NamedParameter.string
import anorm.sqlToSimple
import javax.inject.Inject
import com.aulatec.egress.Departure
import play.api.cache.CacheApi
import play.api.db.Database
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.db.NamedDatabase
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import com.aulatec.users.Alumno
import com.aulatec.egress.Turno
import com.aulatec.users.Id
import com.aulatec.push.PushMessage
import com.aulatec.push.PushService
import com.aulatec.push.Data
import com.aulatec.Dao
import com.aulatec.users.UserService
import com.aulatec.users.Responsable
import com.aulatec.egress.EgressManagerService

class EgressManagerController @Inject() (cache: CacheApi, pushSevice: PushService, userService: UserService, egressService: EgressManagerService, @NamedDatabase("default") db: Database) extends Controller {

  def getAssignedStudents = AuthenticatedResp.async { request =>

    val resp = request.user

    egressService.getAssignedStudents(resp).map { departures =>
      Ok(Json.toJson(departures))
    }

  }

  def getAvailableShifts() = AuthenticatedResp.async { request =>

    val resp = request.user

    egressService.getAvailableShifts(resp) map { turnos =>
      Ok(Json.toJson(turnos))
    }

  }

  def departureRequest() = AuthenticatedResp.async(parse.json) { request =>
    request.body.validate[List[Id]].fold(error => {
      Future.successful(BadRequest(JsError.toJson(error)))
    }, studentIds => {

      egressService.addDepartureRequest(studentIds) map { results =>
        val x = results.map { case (id, result) => (id.toString() -> result) }
        Ok(Json.toJson(x))
      }

    })
  }

  def departureNotification() = AuthenticatedResp { request =>
    NotImplemented
  }

  def getUndispatchedDepartureRequests = AuthenticatedResp { request =>
    NotImplemented
  }

  def setEgressStatus() = AuthenticatedResp { request =>
    NotImplemented
  }

}
