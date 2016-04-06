package model

import play.api.libs.json.Json

case class Responsable(
  id: Id,
  familia: Id,
  nombre: String,
  apellido: String,
  dni: Int,
  celular: String,
  email: String,
  pais: String,
  esDocente: Boolean,
  deviceType: Option[String],
  deviceRegId: Option[String]
)

object Responsable {
  implicit val responsableFormat = Json.format[Responsable]
}