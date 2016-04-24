package com.aulatec.push

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import com.aulatec.users.Device

case class PushMessage[T](to: Device, data: Data[T])

object PushMessage {

  implicit def pushMessageWrites[T](implicit wrt: Writes[Data[T]]) = new Writes[PushMessage[T]] {
    def writes(message: PushMessage[T]) = Json.obj(
      "to" -> message.to.deviceId,
      "data" -> Json.toJson(message.data))
  }
}

case class Data[T](event: String, value: T)

object Data {

  implicit def dataWrites[T](implicit wrt: Writes[T]) = new Writes[Data[T]] {
    def writes(data: Data[T]) = Json.obj(
      "event" -> data.event,
      "value" -> Json.toJson(data.value))
  }
}

