package com.aulatec

import anorm._
import anorm.SQL
import anorm.SqlParser._
import com.aulatec.users.Alumno
import com.aulatec.egress.Departure
import com.aulatec.users.Responsable
import com.aulatec.egress.Turno

object Dao {

  private val responsableParser = Macro.indexedParser[Responsable]
  private val alumnoParser = Macro.indexedParser[Alumno]
  private val shiftParser = Macro.indexedParser[Turno]
  private val parser = (str("name") ~ int("population")).map { case a => 2 }
  private val departureParser = alumnoParser ~ int("resp_id") ~ bool("is_titular") ~ get[Option[Boolean]]("checkout") map {
    case student ~ respId ~ isTitular ~ checkoutOpt =>
      Departure(student, respId, isTitular, checkoutOpt.isDefined, checkoutOpt.getOrElse(false))
  }

  private val alumnoByIdQuery = SQL(s"""
SELECT 
  id, nombre, apellido, aula
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
  a.id, a.nombre, a.apellido, a.aula, {respId} AS resp_id, true AS is_titular, b.checkout 
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
  a.id, a.nombre, a.apellido, a.aula, b.resp AS resp_id, false AS is_titular, c.checkout
FROM
  aulatec.alumno a INNER JOIN 
  aulatec.fam_alu_resp b 
    on (a.id = b.alumno) LEFT OUTER JOIN aulatec.log_alumnos_retira c
    on (
      b.alumno = c.alumno AND 
      c.fecha_retiro = {currentDate})
WHERE
 b.resp = {respId} AND 
 {currentDate} BETWEEN valido_desde AND valido_hasta
"""

    SQL(s"""
SELECT id, nombre, apellido, aula, resp_id, is_titular, checkout
FROM 
  ($ownStudentsQuery UNION ALL $authorizedStudentsQuery) AS assigned_students 
""")

  }

  val insertDepartureRequest = SQL(s"""
INSERT INTO aulatec.log_alumnos_retira(alumno, resp, fecha_retiro, hora_retiro)
VALUES ({studentId}, {respId}, {egressDate}, {egressTime})
""")

  val departuresByDispatcherQuery = {

    val dispatcherClassRoomsByDate = """
SELECT 
  aula 
FROM 
  aulatec.log_docente_aula 
WHERE 
resp = {dispatcher} AND
fecha_retiro = {currentDate}
"""
    SQL(s"""
SELECT
  a.id, a.nombre, a.apellido, a.aula, b.resp AS resp_id, a.familia = c.familia AS is_titular, b.checkout
FROM
  aulatec.alumno a INNER JOIN 
  aulatec.log_alumnos_retira b 
    on (a.id = b.alumno) INNER JOIN aulatec.responsable c 
    on(b.resp = c.id) 
WHERE
 fecha_retiro = {currentDate} AND 
 a.aula IN ($dispatcherClassRoomsByDate) 
""")
  }

  val login = (registerDevice, loguinQuery, responsableParser)
  val alumnoById = (alumnoByIdQuery, alumnoParser)
  val responsableById = (responsableByIdQuery, responsableParser)
  val currentShift = (currentShiftsBySchoolQuery, shiftParser)
  val assignedStudentsByResp = (assignedStudentsByRespQuery, departureParser)
  val departuresByDispatcher = (departuresByDispatcherQuery, departureParser)
}