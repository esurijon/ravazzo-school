package controllers

import anorm.Macro
import anorm.RowParser
import anorm.SQL
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
  private val departureParser = Macro.namedParser[Departure]

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
	id, familia, nombre, apellido, dni, celular, email,  pais, es_docente
FROM 
  aulatec.responsable
WHERE 
  id = {respId};
""")

  private val loguinQuery = SQL(s"""
SELECT 
	id, familia, nombre, apellido, dni, celular, email,  pais, es_docente
FROM 
  aulatec.responsable
WHERE 
  email = {email} AND
  password = {ofuscatedPassword} AND
  es_docente = {asTeacher};
""")

  private val currentShiftsBySchoolQuery = SQL(s"""
SELECT 
	id, cole, texto, hora_inicio, hora_fin
FROM 
  aulatec.turno
WHERE 
  cole = {schoolId} AND
  {currentTime} BETWEEN horaInicio AND horaFin
""")

  private val assignedStudentsByRespQuery = {
    val ownStudentsQuery = s"""
SELECT
  id, true AS 'isTitular'
FROM
  aulatec.alumno
WHERE
 familia =  {familiaId}
"""
    val authorizedStudentsQuery = s"""
SELECT
  id, false as 'isTitular'
FROM
  aulatec.fam_alu_resp
WHERE
 resp = {respId} AND 
 {currentDate} BETWEEN valido_desde AND valido_hasta
"""

    val departedStudents = s"""
SELECT 
  alumno
FROM
  log_alumnos_retira Alum_con_checkin  for all entries of T_ALUMNOS.AUTORIZADOS where Id_Alumno = T_ALUMNOS.AUTORIZADOS-Id_Alumno and Fecha.Retiro = sy-datum
"""

    SQL(s"""
SELECT * 
FROM 
  ($ownStudentsQuery UNION ALL $authorizedStudentsQuery) as assigned_students 
WHERE 
  id NOT IN ($departedStudents)
""")

  }

  val login = Dao(loguinQuery, responsableParser)
  val alumnoById = Dao(alumnoByIdQuery, alumnoParser)
  val responsableById = Dao(responsableByIdQuery, responsableParser)
  val currentShift = Dao(currentShiftsBySchoolQuery, shiftParser)
  val assignedStudentsByResp = Dao(assignedStudentsByRespQuery, departureParser)
}