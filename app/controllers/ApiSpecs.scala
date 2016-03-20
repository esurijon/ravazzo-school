package controllers

import scala.concurrent.Future
import com.iheart.playSwagger.SwaggerSpecGenerator
import javax.inject.Inject
import play.api.cache.Cached
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.routing.Router
import play.api.libs.json.Json

class ApiSpecs @Inject() (cached: Cached, router: Router) extends Controller {

  implicit val cl = getClass.getClassLoader

  // The root package of your domain classes, play-swagger will automatically generate definitions when it encounters class references in this package.
  // In our case it would be "com.iheart", play-swagger supports multiple domain package names
  private lazy val generator = SwaggerSpecGenerator("model", "push")

  def specs = cached("swaggerDef") {
    //it would be beneficial to cache this endpoint as we do here, but it's not required if you don't expect much traffic.   
    Action.async { _ =>
      //generate() can also taking in an optional arg of the route file name.
      Future.fromTry(generator.generate()).map(Ok(_))
    }
  }

  def api = Action { implicit request =>
    val myroutes = router.documentation.map {
      x => Json.obj("http_method" -> x._1, "path" -> x._2, "scala" -> x._3)
    }
    Ok(Json.toJson(myroutes))
  }

}