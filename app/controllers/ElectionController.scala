package controllers

import javax.inject._
import scala.concurrent.{ExecutionContext, Future, Promise}
import play.api.Logger
import play.api.libs.ws._
import play.api.mvc._ 
import play.api.cache._ 
import akka.actor.ActorSystem
import models._
import scala.util.{Try, Success, Failure}
import play.api.libs.json._

@Singleton
class ElectionController @Inject() 
  (ws: WSClient)
  (cached: Cached) 
  (actorSystem: ActorSystem)
  (configuration: services.Config) 
  (implicit exec: ExecutionContext) 
  extends Controller  
  with ElectionMachineJsonIo
  with BoardJSONFormatter
{
  
  def create = Action.async { 
    request => { 
      val promise = Promise[Result]()
      var message = ""
      
      request.body.asJson flatMap {
        json => 
          Try[JsElection[JsElectionState]] {
            val b64 = new Base64Message(json)
            // for some reason Fiware doesn't like the '=' character on a String (or \")
            message = b64.toString().replace('=', '.')
            json.as[JsElection[JsElectionState]]
          } toOption
      } match {
        case Some(e) => 
           val post = PostRequest(message, UserAttributes("election", "create", None, None))
           Logger.info("Posting to agora-board !\n" + Json.toJson(post))
           
           val futureResponse: Future[WSResponse] = 
           ws.url(s"${configuration.agoraboard.url}/bulletin_post")
           .withHeaders(
               "Content-Type" -> "application/json",
               "Accept" -> "application/json")
           .post(Json.toJson(post))
                      
           futureResponse onComplete {
             case Success(response) =>
               Logger.info("Success !\n" + response.json)
               response.json.asOpt[BoardAttributes] match {
                 case Some(attr) =>
                   // The post message index will be the unique id of the election
                   promise.success(Ok(attr.index))
                 case None => 
                   promise.success(BadRequest(response.json))
               }               
             case Failure(e) =>
               Logger.info(s"Failure: $e")
               promise.success(BadRequest(s"$e"))
           }
        case None => 
          Logger.info(s"Failure: None")
          promise.success(BadRequest("None"))
      }
      promise.future
    }
  }
}