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

class UserController @Inject() (@NamedDatabase("default") db: Database) extends Controller {

  def login() = Action(parse.json) { request =>
    request.body.validate[LoginRequest].fold(error => {
      BadRequest(JsError.toJson(error))
    }, loginRequest => {
      val ofuscatedPassword = loginRequest.password
      val responsableOpt = db.withConnection { implicit c =>
        Dao.login.query
          .on("email" -> loginRequest.email)
          .on("ofuscatedPassword" -> ofuscatedPassword)
          .on("asTeacher" -> loginRequest.asTeacher)
          .as(Dao.login.parser.singleOpt)
      }
      responsableOpt.fold {
        Unauthorized("Invalid email/password")
      } {
        responsable =>
          Ok(Json.toJson(responsable))
            .withSession("responsable" -> Json.toJson(responsable).toString())
      }

    })

  }

  def getResponsable(respId: Id) = AuthenticatedResp { request =>
    val responsableOpt = db.withConnection { implicit c =>
      Dao.responsableById.query
        .on("respId" -> respId)
        .as(Dao.responsableById.parser.singleOpt)
    }
    responsableOpt.fold {
      NotFound(request.toString())
    } {
      responsable => Ok(Json.toJson(responsable))
    }

  }

  def getStudent(studentId: Id) = AuthenticatedResp { request =>

    val studentOpt = db.withConnection { implicit c =>
      Dao.alumnoById.query
        .on("studentId" -> studentId)
        .as(Dao.alumnoById.parser.singleOpt)
    }
    studentOpt.fold {
      NotFound(request.toString())
    } {
      student => Ok(Json.toJson(student))
    }

  }
}
