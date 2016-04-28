package models

import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.functional.syntax._

case class JsCryptoSettings(group: String, generator: String)
case class JsElectionState(id: String, cSettings: JsCryptoSettings)
case class JsCreated(id: String, cSettings: JsCryptoSettings, uid: String)
case class JsShares(val shares: (String, String))
case class JsElection(level: Int, state: JsCreated )

trait ElectionJsonFormatter {
  implicit val jsCryptoSettingsWrites: Writes[JsCryptoSettings] = (
    (JsPath \ "group").write[String] and
    (JsPath \ "generator").write[String]
  )(unlift(JsCryptoSettings.unapply))
  
  implicit val jsJsCreatedWrites: Writes[JsCreated] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "cSettings").write[JsCryptoSettings] and
    (JsPath \ "uid").write[String]
  )(unlift(JsCreated.unapply))
  
  implicit val jsElectionStateWrites: Writes[JsElectionState] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "cSettings").write[JsCryptoSettings]
  )(unlift(JsElectionState.unapply))
  
  implicit def jsJsElectionWrites: Writes[JsElection] = (
    (JsPath \ "level").write[Int] and
    (JsPath \ "state").write[JsCreated]
  )(unlift(JsElection.unapply))
  
  implicit val jsCryptoSettingsReads: Reads[JsCryptoSettings] = (
    (JsPath \ "group").read[String] and
    (JsPath \ "generator").read[String]
  )(JsCryptoSettings.apply _)
  
  implicit val jsElectionStateReads: Reads[JsElectionState] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "cSettings").read[JsCryptoSettings]
  )(JsElectionState.apply _)
  
  
  implicit val jsJsCreatedReads: Reads[JsCreated] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "cSettings").read[JsCryptoSettings] and
    (JsPath \ "uid").read[String]
  )(JsCreated.apply _)
  
  implicit def jsJsElectionReads: Reads[JsElection] = (
    (JsPath \ "level").read[Int] and
    (JsPath \ "state").read[JsCreated] 
  )(JsElection.apply _)
}