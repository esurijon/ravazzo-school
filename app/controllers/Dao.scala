package controllers

import anorm._
import anorm.RowParser
import anorm.SQL
import anorm.SqlParser._
import anorm.SqlQuery
import model.Alumno
import model.Departure
import model.Responsable
import model.Turno

case class Dao[T](query: SqlQuery, parser: RowParser[T])

object Dao {

  private val responsableParser = Macro.indexedParser[Responsable]
  private val alumnoParser = Macro.indexedParser[Alumno]
  private val shiftParser = Macro.indexedParser[Turno]
  private val parser = (str("name") ~ int("population")).map { case a => 2 }
  private val departureParser = int("aloptumno") ~ bool("is_titular") ~ get[Option[Boolean]]("checkout") map {
    case student ~ isTitular ~ checkoutOpt =>
      Departure(student, isTitular, checkoutOpt.isDefined, checkoutOpt.getOrElse(false))
  }

  private val alumnoByIdQuery = SQL(s"""
SELECT 
  id, cole, familia, nombre, apellido, aula, pais
FROM 
  aulatec.alumno
WHERE 
  id = {studentId}
""")

  val responsableByIdQuery = SQL(s"""
SELECT 
	id, familia, nombre, apellido, dni, celular, email,  pais, es_docente, device_type, device_reg_id
FROM 
  aulatec.responsable
WHERE 
  id = {respId}
""")

  private val loguinQuery = SQL(s"""
SELECT 
	id, familia, nombre, apellido, dni, celular, email,  pais, es_docente, device_type, device_reg_id
FROM 
  aulatec.responsable
WHERE 
  email = {email} AND
  password = {ofuscatedPassword}
""")

  private val registerDevice = SQL("""
UPDATE 
  aulatec.responsable 
SET 
  device_type = {deviceType},
  device_reg_id = {deviceRegId} 
WHERE 
  email = {email} AND
  password = {ofuscatedPassword}
""")

  private val currentShiftsBySchoolQuery = SQL(s"""
SELECT 
	id, cole, texto, hora_inicio, hora_fin
FROM 
  aulatec.turno
WHERE 
  cole = {schoolId} AND
  time {currentTime} BETWEEN horaInicio AND horaFin
""")

  private val assignedStudentsByRespQuery = {
    val ownStudentsQuery = s"""
SELECT
  a.id AS alumno, true AS is_titular, b.checkout 
FROM
  aulatec.alumno a LEFT OUTER JOIN aulatec.log_alumnos_retira b
    on (
      a.id = b.alumno AND 
      b.fecha_retiro = {currentDate})
WHERE
 a.familia = {familiaId}
"""
    val authorizedStudentsQuery = s"""
SELECT
  a.alumno AS alumno, false AS is_titular, b.checkout
FROM
  aulatec.fam_alu_resp a LEFT OUTER JOIN aulatec.log_alumnos_retira b
    on (
      a.alumno = b.alumno AND 
      b.fecha_retiro = {currentDate})
WHERE
 a.resp = {respId} AND 
 {currentDate} BETWEEN valido_desde AND valido_hasta
"""

    val departedStudents = s"""
SELECT 
  alumno
FROM
  aulatec.log_alumnos_retira 
WHERE
  Id_Alumno = T_ALUMNOS.AUTORIZADOS-Id_Alumno and Fecha.Retiro = sy-datum
"""

    SQL(s"""
SELECT alumno, is_titular, checkout
FROM 
  ($ownStudentsQuery UNION ALL $authorizedStudentsQuery) AS assigned_students 
""")

  }

  val login = (registerDevice, loguinQuery, responsableParser)
  val alumnoById = Dao(alumnoByIdQuery, alumnoParser)
  val responsableById = Dao(responsableByIdQuery, responsableParser)
  val currentShift = Dao(currentShiftsBySchoolQuery, shiftParser)
  val assignedStudentsByResp = Dao(assignedStudentsByRespQuery, departureParser)
}