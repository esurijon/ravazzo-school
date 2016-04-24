package com.aulatec.users

import play.api.libs.json.Json

case class Device(
  deviceType: String,
  deviceId: String)

object Device {
  implicit val alumnoFormat = Json.format[Device]
}