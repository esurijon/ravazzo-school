package com.aulatec.egress

import play.api.libs.json.Json
import com.aulatec.users.Alumno
import com.aulatec.users.Id
import java.util.Date

case class DepartureLog(student: Id, respo: Id, date: Date)

object DepartureLog {
  implicit val departureLogFormat = Json.format[DepartureLog]
}

