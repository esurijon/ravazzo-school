package model

import java.sql.Time
import play.api.libs.json.Json
import org.joda.time.LocalTime

case class Turno(
  id: Id,
  cole: Id,
  texto: String,
  horaInicio: String,
  horaFin: String)

object Turno {
  implicit val turnoWrites = Json.writes[Turno]
}