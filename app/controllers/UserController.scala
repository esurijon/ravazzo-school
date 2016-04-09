package controllers

import anorm.NamedParameter.string
import anorm.sqlToSimple
import javax.inject.Inject
import com.aulatec.users.LoginRequest
import play.api.db.Database
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.db.NamedDatabase
import anorm.SQL
import com.aulatec.users.Id
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import com.aulatec.Dao
import com.aulatec.users.UserService
import java.sql.SQLException
import com.aulatec.users.Alumno
import anorm.AnormException
import org.omg.CosNaming.NamingContextPackage.NotFound

class UserController @Inject() (userService: UserService) extends Controller {

  def login() = Action.async(parse.json) { request =>
    request.body.validate[LoginRequest].fold(error => {
      Future.successful(BadRequest(JsError.toJson(error)))
    }, loginRequest => {
      userService.login(loginRequest).map {
        _.fold {
          Unauthorized("Invalid email/password")
        } {
          responsable =>
            Ok(Json.toJson(responsable))
              .withSession("responsable" -> Json.toJson(responsable).toString())
        }
      }
    })
  }

  def getResponsable(respId: Id) = AuthenticatedResp.async { request =>
    userService.getResponsable(respId).map { responsable =>
      Ok(Json.toJson(responsable))
    }.recover {
      case AnormException("SqlMappingError(No rows when expecting a single one)") => NotFound(request.toString())
    }
  }

  def getStudent(studentId: Id) = AuthenticatedResp.async { request =>
    userService.getStudent(studentId).map { student =>
      Ok(Json.toJson(student))
    }.recover {
      case AnormException("SqlMappingError(No rows when expecting a single one)") => NotFound(request.toString())
    }
  }

}
