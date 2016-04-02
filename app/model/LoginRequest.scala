package model

import java.util.Date
import play.api.libs.json.Json

case class LoginRequest(email: String, password: String, asTeacher: Boolean)

object LoginRequest {
  implicit val loguinRequestReads = Json.reads[LoginRequest]
}