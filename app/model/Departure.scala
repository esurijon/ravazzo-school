package model

import play.api.libs.json.JsObject
import com.fasterxml.jackson.annotation.JsonFormat
import play.api.libs.json.Json

case class Departure(parent: Id, gate: Id, dispatcher: Id, classroom: Id)
