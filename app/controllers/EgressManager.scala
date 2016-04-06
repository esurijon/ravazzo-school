package controllers

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalTime

import com.google.inject.ImplementedBy

import anorm.Macro
import anorm.NamedParameter.string
import anorm.sqlToSimple
import javax.inject.Inject
import model.Departure
import model.Departure.departureFormat
import model.Turno
import play.api.cache.CacheApi
import play.api.db.Database
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.db.NamedDatabase
import push.GcmPushService
import push.PushService

class EgressManagerController @Inject() (cache: CacheApi, pushSevice: PushService, @NamedDatabase("default") db: Database) extends Controller {

  private val turnoParser = Macro.indexedParser[Turno]

  def getAssignedStudents = AuthenticatedResp { request =>

    val resp = request.user
    val currentDate = new java.util.Date(0)//new DateTime(DateTimeZone.getDefault)

    val departures = db.withConnection { implicit c =>
      Dao.assignedStudentsByResp.query
        .on("familiaId" -> resp.familia)
        .on("respId" -> resp.id)
        .on("currentDate" -> currentDate)
        .as(Dao.assignedStudentsByResp.parser.*)
    }
    Ok(Json.toJson(departures))
  }

  def getAvailableShifts() = AuthenticatedResp { request =>

    val currentTime = new java.util.Date(0) //new LocalTime(DateTimeZone.getDefault)

    val turnos = db.withConnection { implicit c =>
      Dao.currentShift.query
        .on("schoolId" -> 22)
        .on("currentTime" -> currentTime)
        .as(Dao.currentShift.parser.*)
    }
    Ok(Json.toJson(turnos))

  }

  def departureRequest() = AuthenticatedResp { request =>
    NotImplemented
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
