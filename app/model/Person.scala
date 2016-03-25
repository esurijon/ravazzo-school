package model

import java.net.URI
import java.util.Date

class Person(
  id: Id,
  firstName: String,
  lastName: String,
  nickName: String,
  birthDate: Date,
  picture: Option[URI])

class User(
  id: Id,
  firstName: String,
  lastName: String,
  nickName: String,
  birthDate: Date,
  picture: Option[URI],
  email: String,
  password: String,
  cellPhoneNumber: String) extends Person(id, firstName, lastName, nickName, birthDate, picture)