package controllers.swagger

import javax.inject._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._ 
import play.api.cache._ 
import com.iheart.playSwagger.SwaggerSpecGenerator

class ApiSpecs @Inject() (cached: Cached) extends Controller {
  implicit val cl = getClass.getClassLoader

  // The root package of your domain classes, play-swagger will automatically generate definitions when it encounters class references in this package.
  // In our case it would be "com.iheart", play-swagger supports multiple domain package names
  val domainPackage = "com.agora-election-board"  
  val secondDomainPackage = "YOUR.OtherDOMAIN.PACKAGE"
  private lazy val generator = SwaggerSpecGenerator(domainPackage, secondDomainPackage)

  def specs = cached("swaggerDef") {  //it would be beneficial to cache this endpoint as we do here, but it's not required if you don't expect much traffic.   
     Action.async { _ =>
        Future.fromTry(generator.generate()).map(Ok(_)) //generate() can also taking in an optional arg of the route file name. 
      }           
  }

}