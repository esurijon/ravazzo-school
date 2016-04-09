package com.aulatec.egress

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalTime

import com.google.inject.ImplementedBy

import anorm._
import anorm.NamedParameter.string
import anorm.sqlToSimple
import javax.inject.Inject
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
import com.aulatec.users.Id
import com.aulatec.push.PushMessage
import com.aulatec.push.PushService
import com.aulatec.push.Data
import com.aulatec.Dao
import com.aulatec.users.UserService
import com.aulatec.users.Responsable
import javax.inject.Singleton
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Singleton
class EgressManagerService @Inject() (pushSevice: PushService, userService: UserService, @NamedDatabase("default") db: Database) extends Controller {

  def getAssignedStudents(resp: Responsable): Future[List[Departure]] = Future {

    val currentDate = new DateTime(DateTimeZone.getDefault)

    db.withConnection { implicit c =>
      Dao.assignedStudentsByResp._1
        .on("familiaId" -> resp.familia)
        .on("respId" -> resp.id)
        .on("currentDate" -> currentDate.toDate())
        .as(Dao.assignedStudentsByResp._2.*)
    }

  }

  def getAvailableShifts(resp: Responsable): Future[List[Turno]] = Future {

    val currentTime = new LocalTime(DateTimeZone.getDefault)

    db.withConnection { implicit c =>
      Dao.currentShift._1
        .on("schoolId" -> 22)
        .on("currentTime" -> currentTime.toString())
        .as(Dao.currentShift._2.*)

    }
  }

  def addDepartureRequest(resp: Responsable, studentIds: List[Id]): Future[Map[Id, Boolean]] = {

    val futureResults = studentIds map { id =>

      for {
        student <- userService.getStudent(id)
        dispatcher <- getStudentDispatcher(student)
        _ <- logDeparture(resp, student)
        pushResult <- sendMessage(student, dispatcher)
      } yield (pushResult)

    }

    Future.sequence(futureResults).map(_.zipWithIndex.map {
      case (result, idx) =>
        val studentId = studentIds(idx)
        (studentId -> result.isRight)
    }.toMap)

  }

  private def getStudentDispatcher(student: Alumno): Future[Responsable] = {
    userService.getResponsable(3)
  }

  private def logDeparture(resp: Responsable, student: Alumno): Future[Unit] = Future {
    val currentDate = new DateTime(DateTimeZone.getDefault)
    val currentTime = new LocalTime(DateTimeZone.getDefault)

    db.withConnection { implicit c =>
      Dao.insertDepartureRequest
        .on("studentId" -> student.id)
        .on("respId" -> resp.id)
        .on("egressDate" -> currentDate.toDate)
        .on("egressTime" -> currentTime.toString)
        .execute()
    }

  }

  private def sendMessage(student: Alumno, resp: Responsable): Future[Either[String, Unit]] = {
    resp.deviceRegId.fold[Future[Either[String, Unit]]] {
      Future.successful(Left(s"No device attached to user ${resp.id}"))
    } { deviceRegId =>
      val to = deviceRegId
      val message = PushMessage(to, Data("departureRequest", student))
      pushSevice.sendMessage(message)
    }
  }

}
