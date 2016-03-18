package model

import play.api.libs.json.JsObject
import com.fasterxml.jackson.annotation.JsonFormat
import play.api.libs.json.Json

case class ChatInboundMessage(sender: String, message: String, clientSentTime: Long)

object ChatInboundMessage {
  implicit val chatInboundMessageReads = Json.reads[ChatInboundMessage]
}

case class ChatOutboundMessage(sender: String, message: String, clientSentTime: Long, serverRecieveTime: Long, serverDispatchTime: Long)

object ChatOutboundMessage {
  implicit val chatOutboundMessageWrites = Json.writes[ChatOutboundMessage]
}
