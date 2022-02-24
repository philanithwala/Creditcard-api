package gateways.cscards.api.client
import gateways.cscards.api.model._
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import scala.concurrent.duration._
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext





class CsCardsClient  @Inject() (ws: WSClient){
  def getCreditCards(name: String, creditScore: Double): Future[WSResponse]={
  val url: String = "https://app.clearscore.com/api/global/backend-tech-test/v1/cards"

  val request: WSRequest = ws.url(url)
  val complexRequest: WSRequest =
    request
      .addHttpHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis)
    val data = Json.obj(
      "name" -> name,
      "creditScore" -> creditScore
    )
   complexRequest.post(data)
  }

}



