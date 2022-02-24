package gateways.scoredcards.api.client

import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


class ScoredCardClient  @Inject() (ws: WSClient){
  def getScoredCards(name: String, score: Double, salary: Double): Future[WSResponse]={
    val url: String = "https://app.clearscore.com/api/global/backend-tech-test/v2/creditcards"

    val request: WSRequest = ws.url(url)
    val complexRequest: WSRequest =
      request
        .addHttpHeaders("Accept" -> "application/json")
        .withRequestTimeout(10000.millis)
    val data = Json.obj(
      "name" -> name,
      "score" -> score,
      "salary" -> salary
    )
    complexRequest.post(data)
  }

}