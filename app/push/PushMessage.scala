package push

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue

case class Notification(title: String, body: String, icon: Option[String]) {
  def this(title: String, body: String) = this(title, body, None)
}

object Notification {
  implicit val notificationWrites = Json.writes[Notification]
}

case class PushMessage(to: String, notification: Option[Notification], data: Option[JsValue]) {
  def this(to: String, notification: Notification, data: JsObject) = this(to, Some(notification), Some(data))
  def this(to: String, notification: Notification) = this(to, Some(notification), None)
  def this(to: String, data: JsValue) = this(to, None, Some(data))
}

object PushMessage {
  implicit val pushMessageWrites = Json.writes[PushMessage]
}
