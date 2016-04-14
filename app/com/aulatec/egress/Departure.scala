package com.aulatec.egress

import play.api.libs.json.Json
import com.aulatec.users.Alumno
import com.aulatec.users.Id

case class Departure(student: Alumno, respId: Id, isTitular: Boolean, hasCheckin: Boolean, hasCheckout: Boolean)

object Departure {
  implicit val departureFormat = Json.format[Departure]
}
