import scala.concurrent.Future
import model.Responsable
import play.api.libs.json.Json
import play.api.mvc.ActionBuilder
import play.api.mvc.ActionTransformer
import play.api.mvc.Request
import play.api.mvc.WrappedRequest
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc.RequestHeader

package object controllers {

  private def responsableFromSession(req: RequestHeader): Option[Responsable] = req.session.get("responsable") map {
    Json.parse(_).as[Responsable]
  }

  object AuthenticatedResp extends AuthenticatedBuilder(req => responsableFromSession(req))

}