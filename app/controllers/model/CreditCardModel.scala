package controllers.model

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class CreditCard(provider: String,
                           name: String,
                           apr: Double,
                           cardScore: Double
                          )

  object CreditCard{

implicit val creditCardWrites = new Writes[CreditCard] {
def writes(creditCard: CreditCard) = Json.obj(
"provider"  -> creditCard.provider,
  "name" -> creditCard.name,
  "apr" -> creditCard.apr,
  "cardScore" -> creditCard.cardScore
)
}
    }




case class CreditCardRequest(name: String,creditScore: Int,salary: Int)