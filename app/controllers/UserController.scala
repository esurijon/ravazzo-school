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

  private val responsableParser = Macro.indexedParser[Responsable]
  private val alumnoParser = Macro.indexedParser[Alumno]

  def login(loginRequest: LoginRequest) = Action {

    val ofuscatedPassword = loginRequest.password
    val loguinQuery = s"""
SELECT 
	id, familia, nombre, apellido, dni, celular, email,  pais, es_docente
FROM 
  aulatec.responsable
WHERE 
  email = '${loginRequest.email}' AND
  password = '$ofuscatedPassword' AND
  es_docente = ${loginRequest.asTeacher};
"""
    val responsable = db.withConnection { implicit c =>
      SQL(loguinQuery).as(alumnoParser.single)
    }
    Ok(Json.toJson(responsable))

  }

  def getResponsable(respId: Id) = Action {

    val responsableByIdQuery = s"""
SELECT 
	id, familia, nombre, apellido, dni, celular, email,  pais, es_docente
FROM 
  aulatec.responsable
WHERE 
  id = $respId;
"""
    val responsable = db.withConnection { implicit c =>
      SQL(responsableByIdQuery).as(alumnoParser.single)
    }
    Ok(Json.toJson(responsable))

  }

  def getStudent(alumnoId: Id) = Action {

    val alumnoByIdQuery = s"""
SELECT 
	id, cole, familia, nombre, apellido, aula, pais,
FROM 
  aulatec.alumno
WHERE 
  id = $alumnoId;
"""
    val alumno = db.withConnection { implicit c =>
      SQL(alumnoByIdQuery).as(alumnoParser.single)
    }
    Ok(Json.toJson(alumno))

  }
}
