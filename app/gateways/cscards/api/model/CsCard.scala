package gateways.cscards.api.model

import controllers.model.CreditCard
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

case class CsCard(cardName: String,
                  apr: Double,
                 eligibility: Double){
  def toCreditCard: CreditCard =
    CreditCard( provider = "CSCards", apr = apr, cardScore = BigDecimal(eligibility*10/Math.pow(apr,2)).
      setScale(3, BigDecimal.RoundingMode.HALF_UP)
      .toDouble,name = cardName)
}
object CsCard {
  lazy implicit val csCardReads: Reads[CsCard] = (
    (JsPath \ "cardName").read[String] and
      (JsPath \ "apr").read[Double] and
      (JsPath \ "eligibility").read[Double]
    )(CsCard.apply _)


}
