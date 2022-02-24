package controllers

import controllers.model.{CreditCard, CreditCardRequest}
import play.api.libs.json.{JsPath, _}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject
import gateways.cscards.api.client._
import gateways.cscards.api.model.CsCard
import gateways.scoredcards.api.client.ScoredCardClient
import gateways.scoredcards.api.model.ScoredCard
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.JsError.toJson
import play.api.libs.ws._
import play.api.libs.functional.syntax._

import scala.concurrent.{ExecutionContext, Future}

class CreditCardController @Inject()(implicit  ec: ExecutionContext,ws: WSClient, val controllerComponents: ControllerComponents) extends BaseController {





  def getCreditCards(): Action[AnyContent] = Action.async{ request =>
    val postValues = request.body.asJson
    lazy implicit val creditCardReads: Reads[CreditCardRequest] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "creditScore").read[Int] and
        (JsPath \ "salary").read[Int]
      )(CreditCardRequest.apply _)

    val json =postValues.get
    json.validate[CreditCardRequest] match {
      case JsSuccess(creditCardReq, _) =>
        val csCardsClient = new CsCardsClient(ws);
        val scoredCardClient = new ScoredCardClient(ws)

        val httpResponses = for {
          result1 <- csCardsClient.getCreditCards(creditCardReq.name, creditCardReq.creditScore)
          result2 <-  scoredCardClient.getScoredCards(creditCardReq.name, creditCardReq.creditScore,creditCardReq.salary)
        } yield List(result1, result2)

        httpResponses.map{
          responses=>{
            responses.forall(_.status==200) match {
              case true =>
                val csCardResponse = Json.parse(responses(0).body).asOpt[Seq[CsCard]]
                println(csCardResponse)
                val scoredCardResponse = Json.parse(responses(1).body).asOpt[Seq[ScoredCard]]
                if (csCardResponse.nonEmpty && scoredCardResponse.nonEmpty) {
                  val creditCards = csCardResponse.get.map(a => a.toCreditCard) ++
                   scoredCardResponse.get.map(scoredCard => scoredCard.toCreditCard)

                  val sortedCreditCards = creditCards.sortBy(creditCard => creditCard.cardScore)
                 Ok(Json.toJson(sortedCreditCards))

                }
                else BadRequest
              case _ => InternalServerError(
                if (responses(0).status !=200)
                  responses(0).body
                else responses(1).body
              )
            }
          }
    }

      case e: JsError=> Future.successful(BadRequest(toJson(e)))
      }

    }

}



