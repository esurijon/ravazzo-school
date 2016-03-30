package model

import play.api.libs.json.Json

case class Alumno(
  id: Int,
  cole: Int,
  familia: Int,
  nombre: String,
  apellido: String,
  aula: Int,
  pais: String)

object Alumno {
  implicit val alumnoWrites = Json.writes[Alumno]
}