package com.aulatec.egress

import play.api.libs.json.Json
import com.aulatec.users.Alumno
import com.aulatec.users.Id

case class Departure(student: Alumno, isTitular: Boolean, hasCheckin: Boolean, hasCheckout: Boolean)

object Departure {
  implicit val departureFormat = Json.format[Departure]
}

case class Departure2(responsable: Id, student: Id, dispatcher: Id, classroom: Id, gatekeeper: Id, gate: Id, isTitular: Boolean)

object Departure2 {
  implicit val departure2Format = Json.format[Departure]
}