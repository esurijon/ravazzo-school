package model

import play.api.libs.json.JsObject
import com.fasterxml.jackson.annotation.JsonFormat
import play.api.libs.json.Json

case class Departure(parent: Id, child: Id, dispatcher: Id, classroom: Id, gatekeeper: Id, gate: Id, isTitular: Boolean)

object Departure {
  implicit val departureFormat = Json.format[Departure]
}