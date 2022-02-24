package gateways.scoredcards.api.model

import controllers.model.CreditCard
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

case class ScoredCard(
                       card: String,
                       apr: Double,
                       approvalRating: Double
                     ){
    def toCreditCard: CreditCard =
       CreditCard( provider = "ScoredCards", apr = apr,
         cardScore = BigDecimal(
           approvalRating*100/Math.pow(apr,2)).
           setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble,
         name = card)
}


object ScoredCard {
  lazy implicit val scoredCardReads: Reads[ScoredCard] = (
    (JsPath \ "card").read[String] and
      (JsPath \ "apr").read[Double] and
      (JsPath \ "approvalRating").read[Double]
    )(ScoredCard.apply _)
}
