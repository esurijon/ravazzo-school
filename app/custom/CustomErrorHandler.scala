package custom

import scala.concurrent.Future

import javax.inject.Inject
import javax.inject.Provider
import play.api.Configuration
import play.api.Environment
import play.api.OptionalSourceMapper
import play.api.UsefulException
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.Writes
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.mvc.Results.Status
import play.api.routing.Router

class CustomErrorHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router]) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  private def isJsonResponseExpected(request: RequestHeader): Boolean = {
    request.accepts("application/json")
  }

  override def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    if (isJsonResponseExpected(request)) {
      val body = Json.obj(
        "request" -> (request.method + " " + request.uri),
        "message" -> message)
      Future.successful(Status(statusCode)(body))
    } else {
      super.onClientError(request, statusCode, message)
    }
  }

  override protected def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = onDevServerError(request, exception)

  override protected def onDevServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    if (isJsonResponseExpected(request)) {

      implicit lazy val throwableWrites: Writes[Throwable] = new Writes[Throwable] {
        override def writes(e: Throwable) = Json.obj(
          "message" -> e.getMessage,
          "stackTrace" -> e.getStackTrace.map { _.toString },
          "cause" -> Option(e.getCause))
      }

      val body = Json.obj(
        "request" -> (request.method + " " + request.uri),
        "title" -> exception.title,
        "exception" -> exception)

      Future.successful(Status(500)(body))

    } else {
      super.onDevServerError(request, exception)
    }
  }

  //  override protected def onDevServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
  //    if (isJsonResponseExpected(request)) {
  //
  //      val stackTrace = {
  //        val sWriter = new StringWriter()
  //        val pWriter = new PrintWriter(sWriter);
  //        exception.printStackTrace(pWriter)
  //        pWriter.flush()
  //        sWriter.toString()
  //      }
  //
  //      val body = Json.obj(
  //        "request" -> (request.method + " " + request.uri),
  //        "title" -> exception.title,
  //        "exception" -> exception.getMessage,
  //        "stackTrace" -> stackTrace.split(Properties.lineSeparator + """\t"""),
  //        "cause" -> exception.getCause.toString())
  //      Future.successful(Status(500)(body))
  //    } else {
  //      super.onDevServerError(request, exception)
  //    }
  //  }

}