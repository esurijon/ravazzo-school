package com.aulatec.users

import scala.concurrent.Future

import com.aulatec.Dao

import anorm.NamedParameter.string
import anorm.SQL
import anorm.sqlToSimple
import javax.inject.Inject
import play.api.db.Database
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import play.db.NamedDatabase
import javax.inject.Singleton

@Singleton
class UserService @Inject() (@NamedDatabase("default") db: Database) {

  def login(loginRequest: LoginRequest): Future[Option[Responsable]] = Future {
    val ofuscatedPassword = loginRequest.password
    db.withConnection { implicit c =>

      Dao.login._1
        .on("deviceType" -> loginRequest.deviceType)
        .on("deviceRegId" -> loginRequest.deviceRegId)
        .on("email" -> loginRequest.email)
        .on("ofuscatedPassword" -> ofuscatedPassword)
        .executeUpdate()

      Dao.login._2
        .on("email" -> loginRequest.email)
        .on("ofuscatedPassword" -> ofuscatedPassword)
        .as(Dao.login._3.singleOpt)

    }
  }

  def getResponsable(respId: Id): Future[Responsable] = Future {
    db.withConnection { implicit c =>
      Dao.responsableById._1
        .on("respId" -> respId)
        .as(Dao.responsableById._2.single)
    }
  }

  def getStudent(studentId: Id): Future[Alumno] = Future {

    db.withConnection { implicit c =>
      Dao.alumnoById._1
        .on("studentId" -> studentId)
        .as(Dao.alumnoById._2.single)
    }

  }
}
