package com.aulatec.users

import play.api.libs.json.Json

case class Alumno(
  id: Int,
  nombre: String,
  apellido: String,
  aula: Id)

object Alumno {
  implicit val alumnoFormat = Json.format[Alumno]
}