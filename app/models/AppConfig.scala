package models

case class AppConfig(val boardName: String, val daysData: Int)

object AppConfig {

  import play.api.libs.json._

  implicit val AppConfigToJson: Writes[AppConfig] = Json.writes[AppConfig]
}


