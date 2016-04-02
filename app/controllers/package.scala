import model.Responsable
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Forbidden
import play.api.mvc.Security.AuthenticatedBuilder

package object controllers {

  private def responsableFromSession(req: RequestHeader): Option[Responsable] = req.session.get("responsable") map {
    Json.parse(_).as[Responsable]
  }

  object AuthenticatedResp extends AuthenticatedBuilder(
    req => responsableFromSession(req),
    req => Forbidden(req.toString()))

}