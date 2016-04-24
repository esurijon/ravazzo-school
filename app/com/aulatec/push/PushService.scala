package com.aulatec.push

import scala.Left
import scala.concurrent.Future

import com.google.inject.ImplementedBy

import javax.inject.Inject
import javax.inject.Singleton
import play.api.libs.json.Writes

@ImplementedBy(classOf[MultiPushService])
abstract class PushService {
  def sendMessage[T](message: PushMessage[T])(implicit wrt: Writes[Data[T]]): Future[Either[String, Unit]]
}

@Singleton
class MultiPushService @Inject() (gcm: GcmPushService, smtp: SmtpPushService) extends PushService {
  override def sendMessage[T](message: PushMessage[T])(implicit wrt: Writes[Data[T]]): Future[Either[String, Unit]] = message.to.deviceType match {
    case "Android" => gcm.sendMessage(message)
    case "Email" => smtp.sendMessage(message)
    case other => Future.successful(Left(s"Unknown device type $other for message: $message"))
  }
}
