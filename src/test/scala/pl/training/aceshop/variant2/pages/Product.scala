package pl.training.aceshop.variant2.pages

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Product {

  def details(productName: String): ChainBuilder = exec(
    http(s"Load Product Detail - ${productName}")
      .get(s"/product/${productName}")
  )

  def addToCart(productId: Int): ChainBuilder = exec(
    http(s"Add to Cart: ${productId}")
      .get(s"/cart/add/${productId}")
  )

}
