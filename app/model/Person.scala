package model

import play.api.libs.json.JsObject
import com.fasterxml.jackson.annotation.JsonFormat
import play.api.libs.json.Json

class Person(name: String)

class User(s: String) extends Person(s)
