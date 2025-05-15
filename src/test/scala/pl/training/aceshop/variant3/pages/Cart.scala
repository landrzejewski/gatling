package pl.training.aceshop.variant3.pages

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Cart {

  val view: ChainBuilder = {
    exec(http("View Cart")
      .get("/cart/view")
    )
  }

  def addProduct(productId: Int): ChainBuilder = exec(
    http(s"Increase Qty - ${productId}")
      .get(s"cart/add/${productId}?cartPage=true")
  )

  def removeProduct(productId: Int): ChainBuilder = exec(
    http(s"Decrease Qty - ${productId}")
      .get(s"cart/subtract/${productId}")
  )

  val checkout: ChainBuilder = exec(
    http("Checkout")
      .get("/cart/checkout")
      .check(substring("Your products are on their way to you now!!"))
  )

}
