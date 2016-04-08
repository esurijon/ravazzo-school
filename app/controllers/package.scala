import com.aulatec.users.Responsable
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Unauthorized
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc.Results
import play.api.mvc.ActionRefiner
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc.WrappedRequest

package object controllers {

  private def responsableFromSession(req: RequestHeader): Option[Responsable] = req.session.get("responsable") map {
    Json.parse(_).as[Responsable]
  }

  object AuthenticatedResp extends AuthenticatedBuilder(
    req => responsableFromSession(req),
    req => Unauthorized(req.toString()))

  /*
   * 
   *
   */
  import play.api.mvc._

  class UserRequest[A](val username: Option[String], request: Request[A]) extends WrappedRequest[A](request)

  object UserAction2 extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest] {
    def transform[A](request: Request[A]) = Future.successful {
      new UserRequest(request.session.get("username"), request)
    }
  }

  object UserAction extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest] {
    def transform[A](request: Request[A]) = Future.successful {
      new UserRequest(request.session.get("username"), request)
    }
  }
  class ItemRequest[A](val item: String, request: UserRequest[A]) extends WrappedRequest[A](request) {
    def username = request.username
  }

  def ItemAction(itemId: String) = new ActionRefiner[UserRequest, ItemRequest] {
    def refine[A](input: UserRequest[A]) = Future.successful {
      Left(Results.Accepted)
      Right(new ItemRequest("", input))
    }
  }

  object PermissionCheckAction extends ActionFilter[ItemRequest] {
    def filter[A](input: ItemRequest[A]) = Future.successful {
      if (true)
        Some(Results.Forbidden)
      else
        None
    }
  }

  def tagItem(itemId: String, tag: String) =
    (UserAction andThen ItemAction(itemId) andThen PermissionCheckAction) { request =>
      Results.Ok("User " + request.username + " tagged " + request.item)
    }

}