package com.aulatec.egress

import play.api.libs.json.Json
import com.aulatec.users.Id

case class Turno(
  id: Id,
  cole: Id,
  texto: String,
  horaInicio: String,
  horaFin: String)

object Turno {
  implicit val turnoWrites = Json.writes[Turno]
}