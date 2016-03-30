package controllers

import anorm.SqlParser._
import com.google.inject.ImplementedBy
import anorm._
import javax.inject.Inject
import model.Id
import play.api.cache.CacheApi
import play.api.db.Database
import play.api.libs.json.JsError
import play.api.mvc.Action
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import play.db.NamedDatabase
import push.GcmPushService
import push.PushService
import play.api.libs.json.Json
import model.Departure
import model.Turno
import java.sql.Time
import org.joda.time.LocalTime
import org.joda.time.DateTimeZone
import org.joda.time.DateTime

class EgressManager @Inject() (cache: CacheApi, pushSevice: PushService, @NamedDatabase("default") db: Database) extends Controller {

  private val turnoParser = Macro.indexedParser[Turno]

  private val departureParser = Macro.namedParser[Departure]

  def getAssignedStudents = Action {

    val responsableId = "esurijon"
    val familiaId = "esurijon"
    val currentDate = new DateTime(DateTimeZone.getDefault)

    val ownStudentsQuery = s"""
SELECT
  id, true AS 'isTitular'
FROM
  aulatec.alumno
WHERE
 familia =  $familiaId 
"""
    val authorizedStudentsQuery = s"""
SELECT
  id, false as 'isTitular'
FROM
  aulatec.fam_alu_resp
WHERE
 resp = $responsableId AND 
 '$currentDate' BETWEEN valido_desde AND valido_hasta
"""

    val departedStudents = s"""
SELECT 
  *
FROM
  log_alumnos_retira Alum_con_checkin  for all entries of T_ALUMNOS.AUTORIZADOS where Id_Alumno = T_ALUMNOS.AUTORIZADOS-Id_Alumno and Fecha.Retiro = sy-datum
"""

    val assignedStudentsByRespQuery = s"""
SELECT * 
FROM 
  ($ownStudentsQuery UNION ALL $authorizedStudentsQuery) as assigned_students 
WHERE 
  id NOT IN ($departedStudents)
"""

    val departures = db.withConnection { implicit c =>
      val result = SQL(???).as(departureParser.*)
      result
    }
    Ok(Json.toJson(departures))
  }

  def noop = Action { Ok }
  def noop2(a: Id) = Action { Ok }

  def getAvailableShifts() = Action {

    val currentTime = new LocalTime(DateTimeZone.getDefault)

    val turnosQuery = s"""
SELECT 
	id, cole, texto, hora_inicio, hora_fin
FROM 
  aulatec.turno
WHERE 
  '$currentTime' BETWEEN horaInicio AND horaFin
"""
    val turnos = db.withConnection { implicit c =>
      SQL(turnosQuery).as(turnoParser.*)
    }
    Ok(Json.toJson(turnos))

  }

}
