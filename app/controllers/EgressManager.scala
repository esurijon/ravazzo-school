package controllers

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalTime

import com.google.inject.ImplementedBy

import anorm._
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
import play.api.libs.json.JsArray
import model.Id
import play.api.libs.json.JsError
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import push.PushMessage
import scala.concurrent.Future
import model.Alumno
import push.PushMessage2

class EgressManagerController @Inject() (cache: CacheApi, pushSevice: PushService, @NamedDatabase("default") db: Database) extends Controller {

  private val turnoParser = Macro.indexedParser[Turno]

  def getAssignedStudents = AuthenticatedResp { request =>

    val resp = request.user
    val currentDate = new DateTime(DateTimeZone.getDefault)

    val departures = db.withConnection { implicit c =>
      Dao.assignedStudentsByResp._1
        .on("familiaId" -> resp.familia)
        .on("respId" -> resp.id)
        .on("currentDate" -> currentDate.toDate())
        .as(Dao.assignedStudentsByResp._2.*)
    }
    Ok(Json.toJson(departures))
  }

  def getAvailableShifts() = AuthenticatedResp { request =>

    val currentTime = new LocalTime(DateTimeZone.getDefault)

    val turnos = db.withConnection { implicit c =>
      Dao.currentShift._1
        .on("schoolId" -> 22)
        .on("currentTime" -> currentTime.toString())
        .as(Dao.currentShift._2.*)
    }
    Ok(Json.toJson(turnos))

  }

  def departureRequest() = AuthenticatedResp.async(parse.json) { request =>
    request.body.validate[List[Id]].fold(error => {
      Future.successful(BadRequest(JsError.toJson(error)))
    }, studentIds => {
      val futureResults = studentIds map { id =>
        val to: String = ???
        val data = Alumno(0, "", "")
        val message = PushMessage(to, ???)
        pushSevice.sendMessage(message)
      }

      Future.sequence(futureResults)
        .map(_.zipWithIndex.map {
          case (result, idx) =>
            val studentId = studentIds(idx).toString
            (studentId -> result.isRight)
        }.toMap)
        .map(q => Ok(Json.toJson(q)))

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
