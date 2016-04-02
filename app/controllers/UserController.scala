package controllers

import anorm.Macro
import anorm.SQL
import anorm.sqlToSimple
import javax.inject.Inject
import model.Alumno
import model.Id
import model.LoginRequest
import model.Responsable
import play.api.db.Database
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.db.NamedDatabase

class UserController @Inject() (@NamedDatabase("default") db: Database) extends Controller {

  def login() = Action(parse.json) { request =>
    val loginRequest = request.body.validate[LoginRequest].asOpt.get
    val ofuscatedPassword = loginRequest.password
    val responsable = db.withConnection { implicit c =>
      Dao.login.query
        .on("email" -> loginRequest.email)
        .on("password" -> ofuscatedPassword)
        .on("asTeacher" -> loginRequest.asTeacher)
        .as(Dao.login.parser.single)
    }
    Ok(Json.toJson(responsable))

  }

  def getResponsable(respId: Id) = Action {

    val responsable = db.withConnection { implicit c =>
      Dao.responsableById.query
        .on("respId" -> respId)
        .as(Dao.responsableById.parser.single)
    }
    Ok(Json.toJson(responsable))

  }

  def getStudent(studentId: Id) = Action {

    val alumno = db.withConnection { implicit c =>
      Dao.alumnoById.query
        .on("studentId" -> studentId)
        .as(Dao.alumnoById.parser.single)
    }
    Ok(Json.toJson(alumno))

  }
}
