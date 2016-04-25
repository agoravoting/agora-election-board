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
import services.Subscription

@Singleton
class Accumulator @Inject
  (ws: WSClient)
  (cached: Cached) 
  (actorSystem: ActorSystem)
  (implicit exec: ExecutionContext) 
  extends Controller
  with BoardJSONFormatter
  with FiwareJSONFormatter
  with Subscription
  with ErrorProcessing
{
  
  /*subscribeToEverything()
  
  def subscribeToEverything() = {
    Logger.info(s"Subscribing to everything")
    val acc = new SubscribeRequest("", "", "http://localhost:9500/accumulate")
    val futureResponse: Future[WSResponse] = 
    ws.url(s"http://localhost:9000/bulletin_subscribe")
    .withHeaders(
      "Content-Type" -> "application/json",
      "Accept" -> "application/json")
    .post(Json.toJson(acc))

    futureResponse onComplete {
      case Success(response) =>
        Logger.info(s"Success: ${response.body}")
      case Failure(e) =>
        Logger.info(s"Failure: $e")
    }
  }*/

  def accumulate = Action.async { request =>
    Logger.info("Accumulate")
    request.body.asJson match {
      case Some(json) =>
        json.validate[AccumulateRequest] match {
          case sr: JsSuccess[AccumulateRequest] =>
            val accReq = sr.get
            getSubscription(accReq.subscriptionId) match {
              case Some(reference) =>
                val promise = Promise[Result]
                Logger.info("==============================================")
                Logger.info(s"accumulate: subscriptionId: ${accReq.subscriptionId} reference: ${reference} text: ${json}")
                val futureResponse: Future[WSResponse] = ws.url(reference)
                .withHeaders("Content-Type" -> "application/json",
                             "Accept" -> "application/json")
                .post(json)
                
                futureResponse onComplete {
                  case Success(response) =>
                    val str = response.body
                    promise.success(Ok(response.body))
                    Logger.info(s"Success: ${str}")
                  case Failure(e) =>
                    promise.success(BadRequest(s"$e"))
                    Logger.info(s"Failure: $e")
                }
                promise.future
              case None =>
                val errorText = s"Future failure: subscriptionId not found: ${accReq.subscriptionId}"
                Logger.info(errorText)
                Future {BadRequest(errorText)}
            }
          case e: JsError =>
            val errorText = s"Bad request: invalid AccumulateRequest json: $json\nerror: ${e}\n"
            Logger.info(errorText)
            Future {BadRequest(errorText)}
        }
      case None =>
        val errorText = s"Bad request: not a json or json format error:\n${request.body.asRaw}\n"
        Logger.info(errorText)
        Future {BadRequest(errorText)}
    }
  }
  
  def subscribe = Action.async { request => 
    Logger.info(s"action: subscribe")
    request.body.asJson match {
      case Some(json) =>
        json.validate[SubscribeRequest] match {
          case sr: JsSuccess[SubscribeRequest] =>
            val promise = Promise[Result]
            val subscriber = sr.get
            val acc = new SubscribeRequest(subscriber.section, subscriber.group, "http://localhost:9500/accumulate")
            val futureResponse: Future[WSResponse] = 
            ws.url(s"http://localhost:9000/bulletin_subscribe")
            .withHeaders(
              "Content-Type" -> "application/json",
              "Accept" -> "application/json")
            .post(Json.toJson(acc))
            
            futureResponse onComplete {
              case Success(response) =>
                val subscriptionId = response.body
                Logger.info(s"Success: ${subscriptionId}")
                addSubscription(subscriptionId, subscriber.reference)
                promise.success(Ok(response.body))
              case Failure(e) =>
                Logger.info(s"Failure: $e")
                promise.success(BadRequest(s"$e"))
            }
            promise.future
          case e: JsError =>
            val errorText = s"Bad request: invalid SubscribeRequest json:\n${e}\n"
            Logger.info(errorText)
            Future {BadRequest(errorText)}
        }
      case None =>
        val errorText = s"Bad request: not a json or json format error:\n${request.body.asRaw}\n"
        Logger.info(errorText)
        Future {BadRequest(errorText)}
    }
  }
}