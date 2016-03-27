package model

import java.net.URI
import java.util.Date
import play.api.libs.json.Json

case class Person(
  id: Id,
  firstName: String,
  lastName: String,
  nickName: String,
  role: String)

object Person {
  implicit val personWrites = Json.writes[Person]
}

case class User(
  id: Id,
  firstName: String,
  lastName: String,
  nickName: String,
  birthDate: Date,
  picture: Option[URI],
  email: String,
  password: String,
  cellPhoneNumber: String)