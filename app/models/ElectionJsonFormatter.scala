package models

import shapeless._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.functional.syntax._

case class JsCryptoSettings(group: String, generator: String)
case class JsElectionState(id: String, cSettings: JsCryptoSettings)
case class JsElection[+S <: JsElectionState](level: Int, state: S )

trait ElectionMachineJsonIo {
  implicit val jsCryptoSettingsWrites: Writes[JsCryptoSettings] = (
    (JsPath \ "group").write[String] and
    (JsPath \ "generator").write[String]
  )(unlift(JsCryptoSettings.unapply))
  
  implicit val jsElectionStateWrites: Writes[JsElectionState] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "cSettings").write[JsCryptoSettings]
  )(unlift(JsElectionState.unapply))
  
  implicit def jsJsElectionWrites[S <: JsElectionState](implicit fmt: Writes[S]): Writes[JsElection[S]] = 
  new Writes[JsElection[JsElectionState]] {
    def writes(ts: JsElection[JsElectionState]) = 
    Json.obj(
      "level" -> ts.level,
      "state" -> Json.toJson(ts.state)
    )
  }
  
  implicit val jsCryptoSettingsReads: Reads[JsCryptoSettings] = (
    (JsPath \ "group").read[String] and
    (JsPath \ "generator").read[String]
  )(JsCryptoSettings.apply _)
  
  implicit val jsElectionStateReads: Reads[JsElectionState] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "cSettings").read[JsCryptoSettings]
  )(JsElectionState.apply _)
  
  implicit def jsJsElectionReads[S <: JsElectionState](implicit fmt: Reads[S]): Reads[JsElection[S]] = 
  new Reads[JsElection[S]] {
    def reads(json: JsValue) : JsResult[JsElection[S]] = 
    JsSuccess(new JsElection[S](
      (json \ "level").as[Int],
      (json \ "state").as[S]
    ))
  }
}