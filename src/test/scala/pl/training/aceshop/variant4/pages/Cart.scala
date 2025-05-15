package pl.training.aceshop.variant4.pages

import io.gatling.core.Predef.{exec, _}
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import pl.training.aceshop.variant4.Session

object Cart {

  val view: ChainBuilder = doIf(session => !session("isAuthenticated").as[Boolean])(
    exec(Customer.login)
  ).exec(http("View Cart")
    .get("/cart/view")
    .check(css("#CategoryHeader").is("Cart Overview"))
  )

  def addProduct(productId: Int): ChainBuilder =
    exec(Session.increaseItemsCount)
      .exec(Session.increaseCartTotalBalance)
      .exec(
        http(s"Increase Qty - ${productId}")
          .get(s"cart/add/${productId}?cartPage=true")
      )

  def removeProduct(productId: Int): ChainBuilder =
    exec(Session.decreaseItemsCount)
      .exec(Session.decreaseCartTotalBalance)
      .exec(
        http(s"Decrease Qty - ${productId}")
          .get(s"cart/subtract/${productId}")
      )

  val checkout: ChainBuilder = exec(
    http("Checkout")
      .get("/cart/checkout")
      .check(substring("Your products are on their way to you now!!"))
  )

}
