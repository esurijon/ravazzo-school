package push

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

case class PushMessage(to: String, data: JsValue)

object PushMessage {
  implicit val pushMessageWrites = Json.writes[PushMessage]
}

case class PushMessage2[T](to: String, data: T)

object PushMessage2 {

  implicit def pushMessageWrites[T](implicit wrt: Writes[T]) = new Writes[PushMessage2[T]] {
    def writes(ts: PushMessage2[T]) = Json.obj(
        "to" -> ts.to,
        "data" -> Json.toJson(ts.data)
        )
  }
}
