package com.aulatec.egress

import scala.Left
import scala.concurrent.Future

import org.joda.time.LocalDate
import org.joda.time.LocalTime

import com.aulatec.Dao
import com.aulatec.push.Data
import com.aulatec.push.MultiPushService
import com.aulatec.push.PushMessage
import com.aulatec.push.PushService
import com.aulatec.users.Alumno
import com.aulatec.users.Id
import com.aulatec.users.Responsable
import com.aulatec.users.UserService
import com.google.inject.ImplementedBy

import anorm.NamedParameter.string
import anorm.sqlToSimple
import javax.inject.Inject
import javax.inject.Singleton
import play.api.db.Database
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import play.db.NamedDatabase

@Singleton
class EgressManagerService @Inject() (pushSevice: PushService, userService: UserService, @NamedDatabase("default") db: Database) extends Controller {

  private def current(): (LocalDate, LocalTime) = {
    //(new DateTime(DateTimeZone.getDefault), new LocalTime(DateTimeZone.getDefault))
    (new LocalDate(2016, 3, 1), new LocalTime(12, 15))
  }

  def getAssignedStudents(resp: Responsable): Future[List[Departure]] = Future {

    val (currentDate, _) = current()

    db.withConnection { implicit c =>
      Dao.assignedStudentsByResp._1
        .on("familiaId" -> resp.familia)
        .on("respId" -> resp.id)
        .on("currentDate" -> currentDate.toDate())
        .as(Dao.assignedStudentsByResp._2.*)
    }

  }

  def getAvailableShifts(resp: Responsable): Future[List[Turno]] = Future {

    val (_, currentTime) = current()

    db.withConnection { implicit c =>
      Dao.currentShift._1
        .on("schoolId" -> ???)
        .on("currentTime" -> currentTime.toString())
        .as(Dao.currentShift._2.*)

    }
  }

  def requestDeparture(resp: Responsable, studentIds: List[Id]): Future[Map[Id, String]] = {

    val futureResults = studentIds map { id =>

      for {
        student <- userService.getStudent(id)
        dispatcherOpt <- getStudentDispatcher(student)
        _ <- logDeparture(resp, student)
        pushResult <- pushDepartureRequest(student, dispatcherOpt)
      } yield (pushResult)

    }

    Future.sequence(futureResults).map(_.zipWithIndex.map {
      case (result, idx) =>
        val studentId = studentIds(idx)
        val resultTxt = result.fold[String](identity, _ => "Ok")
        (studentId -> resultTxt)
    }.toMap)

  }

  private def getStudentDispatcher(student: Alumno): Future[Option[Responsable]] = Future {
    val (currentDate, currentTime) = current()

    db.withConnection { implicit c =>
      Dao.studentAssignedDispatcher._1
        .on("studentId" -> student.id)
        .on("egressDate" -> currentDate.toDate)
        .as(Dao.studentAssignedDispatcher._2.singleOpt)
    }

  }

  private def logDeparture(resp: Responsable, student: Alumno): Future[Unit] = Future {
    val (currentDate, currentTime) = current()

    db.withConnection { implicit c =>
      Dao.insertDepartureRequest
        .on("studentId" -> student.id)
        .on("respId" -> resp.id)
        .on("egressDate" -> currentDate.toDate)
        .on("egressTime" -> currentTime.toString)
        .execute()
    }

  }

  private def pushDepartureRequest(student: Alumno, dispatcherOpt: Option[Responsable]): Future[Either[String, Unit]] = {
    dispatcherOpt.fold[Future[Either[String, Unit]]] {
      Future.successful(Left(s"No dispatcher assigned to classroom ${student.aula}"))
    } { dispatcher =>
      dispatcher.device.fold[Future[Either[String, Unit]]] {
        Future.successful(Left(s"No device attached to user ${dispatcher.id}"))
      } { device =>
        val message = PushMessage(device, Data("departureRequest", student))
        pushSevice.sendMessage(message)
      }
    }
  }

  def notifyDeparture(dispatcher: Responsable, studentId: Id): Future[String] = {
    (for {
      student <- userService.getStudent(studentId)
      resp <- getStudentResponsable(studentId)
      _ <- updateDeparture(student)
      pushResult <- pushDepartureNotification(student, resp)
    } yield (pushResult)) map { result =>
      result.fold(identity, _ => "Ok")
    }
  }

  def getStudentResponsable(studentId: Id): Future[Responsable] = Future {
    val (currentDate, currentTime) = current()

    db.withConnection { implicit c =>
      Dao.studentAssignedResp._1
        .on("studentId" -> studentId)
        .on("egressDate" -> currentDate.toDate)
        .as(Dao.studentAssignedResp._2.single)
    }
  }

  private def updateDeparture(student: Alumno): Future[Unit] = Future {
    val (currentDate, currentTime) = current()

    db.withConnection { implicit c =>
      Dao.updateDepartureRequest
        .on("studentId" -> student.id)
        .on("egressDate" -> currentDate.toDate)
        .execute()
    }

  }

  private def pushDepartureNotification(student: Alumno, resp: Responsable): Future[Either[String, Unit]] = {
    resp.device.fold[Future[Either[String, Unit]]] {
      Future.successful(Left(s"No device attached to user ${resp.id}"))
    } { device =>
      val message = PushMessage(device, Data("departureNotification", student))
      pushSevice.sendMessage(message)
    }
  }

  def getDepartureRequests(dispatcher: Responsable): Future[List[Departure]] = Future {
    val (currentDate, _) = current()
    db.withConnection { implicit c =>
      Dao.departuresByDispatcher._1
        .on("dispatcher" -> 3)
        .on("currentDate" -> currentDate.toDate())
        .as(Dao.departuresByDispatcher._2.*)
    }
  }

}
