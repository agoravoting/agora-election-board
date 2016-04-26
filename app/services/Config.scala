package services

import javax.inject._

case class Server(url: String)
case class AgoraBoard(url: String)

@Singleton
class Config @Inject() 
                     (configuration: play.api.Configuration)  
{
  val agoraboard = AgoraBoard(configuration.getString("agoraboard.url").getOrElse("http://localhost:9000"))
  val server = Server(configuration.getString("play.server.http.url").getOrElse("http://localhost:9500"))
}