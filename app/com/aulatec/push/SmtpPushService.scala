package com.aulatec.push

import scala.Left
import scala.Right
import scala.concurrent.Future

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.EmailException
import org.apache.commons.mail.SimpleEmail
import org.slf4j.LoggerFactory

import com.google.inject.ImplementedBy

import javax.inject.Inject
import javax.inject.Singleton
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.json.Writes

@Singleton
class SmtpPushService @Inject() (conf: Configuration) extends PushService {

  private val logger = LoggerFactory.getLogger(classOf[GcmPushService])

  private val host = conf.getString("smtp.host").get
  private val port = conf.getInt("smtp.port").get
  private val userName = conf.getString("smtp.user").get
  private val password = conf.getString("smtp.password").get
  private val sender = conf.getString("smtp.sender").get
  private val subject = conf.getString("smtp.subject").getOrElse("departure notification")

  override def sendMessage[T](message: PushMessage[T])(implicit wrt: Writes[Data[T]]): Future[Either[String, Unit]] = {
    Future {
      val email = new SimpleEmail()
      email.setHostName(host)
      email.setSmtpPort(port)
      email.setAuthenticator(new DefaultAuthenticator(userName, password))
      email.setSSLOnConnect(false)
      email.setFrom(sender)
      email.setSubject(subject)
      email.setMsg(Json.prettyPrint(Json.toJson(message.data)))
      email.addTo(message.to.deviceId)
      email.send()
      Right(())
    } recover {
      case e: EmailException => Left(e.getMessage)
    }
  }
}