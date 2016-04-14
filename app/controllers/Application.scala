package controllers

import scala.concurrent.Future

import com.google.inject.ImplementedBy

import javax.inject.Inject
import play.api.cache.CacheApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import com.aulatec.push.PushService
import play.api.db.Database
import play.api.db.NamedDatabaseProvider
import anorm._

class Application @Inject() (@NamedDatabaseProvider("default") db: Database) extends Controller {

  def deleteDepartureLog() = AuthenticatedResp { request =>

    val count = db.withConnection { implicit c =>
      SQL("DELETE FROM aulatec. aulatec.log_alumnos_retira ").executeUpdate()
    }
    Ok(s"$count")
  }

}
