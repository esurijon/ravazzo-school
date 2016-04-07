package controllers

import anorm.NamedParameter.string
import anorm.sqlToSimple
import javax.inject.Inject
import model.Id
import model.LoginRequest
import play.api.db.Database
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.db.NamedDatabase
import anorm.SQL

class UserController @Inject() (@NamedDatabase("default") db: Database) extends Controller {

  def login() = Action(parse.json) { request =>
    request.body.validate[LoginRequest].fold(error => {
      BadRequest(JsError.toJson(error))
    }, loginRequest => {
      val ofuscatedPassword = loginRequest.password
      db.withConnection { implicit c =>

        Dao.login._1
          .on("deviceType" -> loginRequest.deviceType)
          .on("deviceRegId" -> loginRequest.deviceRegId)
          .on("email" -> loginRequest.email)
          .on("ofuscatedPassword" -> ofuscatedPassword)
          .executeUpdate()

        val responsableOpt = Dao.login._2
          .on("email" -> loginRequest.email)
          .on("ofuscatedPassword" -> ofuscatedPassword)
          .as(Dao.login._3.singleOpt)
        responsableOpt.fold {
          Unauthorized("Invalid email/password")
        } {
          responsable =>
            Ok(Json.toJson(responsable))
              .withSession("responsable" -> Json.toJson(responsable).toString())
        }
      }
    })

  }

  def getResponsable(respId: Id) = AuthenticatedResp { request =>
    val responsableOpt = db.withConnection { implicit c =>
      Dao.responsableById._1
        .on("respId" -> respId)
        .as(Dao.responsableById._2.singleOpt)
    }
    responsableOpt.fold {
      NotFound(request.toString())
    } {
      responsable => Ok(Json.toJson(responsable))
    }

  }

  def getStudent(studentId: Id) = AuthenticatedResp { request =>

    val studentOpt = db.withConnection { implicit c =>
      Dao.alumnoById._1
        .on("studentId" -> studentId)
        .as(Dao.alumnoById._2.singleOpt)
    }
    studentOpt.fold {
      NotFound(request.toString())
    } {
      student => Ok(Json.toJson(student))
    }

  }
}
