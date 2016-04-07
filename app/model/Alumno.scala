package model

import play.api.libs.json.Json

case class Alumno(
  id: Int,
  nombre: String,
  apellido: String)

object Alumno {
  implicit val alumnoFormat = Json.format[Alumno]
}