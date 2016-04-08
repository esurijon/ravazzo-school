package com.aulatec.push

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

case class PushMessage[T](to: String, data: Data[T])

object PushMessage {

  implicit def pushMessageWrites[T](implicit wrt: Writes[Data[T]]) = new Writes[PushMessage[Data[T]]] {
    def writes(message: PushMessage[Data[T]]) = Json.obj(
      "to" -> message.to,
      "data" -> Json.toJson(0/*message.data*/))
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

