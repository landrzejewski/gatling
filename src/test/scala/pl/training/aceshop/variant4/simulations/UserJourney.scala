package pl.training.aceshop.variant4.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

import scala.concurrent.duration._
import pl.training.aceshop.variant4.Session._
import pl.training.aceshop.variant4.pages._

object UserJourney {

  private val LOW_PAUSE = 1.second
  private val HIGH_PAUSE = 3.seconds

  val browseStore: ChainBuilder =
    exec(initSession)
      .exec(StaticPages.home)
      .pause(HIGH_PAUSE)
      .exec(StaticPages.ourStory)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(StaticPages.getInTouch)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .repeat(3) {
        exec(Category.productsByRandomCategory)
          .pause(LOW_PAUSE, HIGH_PAUSE)
          .exec(Category.iterateOverPages)
          .pause(LOW_PAUSE, HIGH_PAUSE)
          .exec(Product.details)
      }

  val abandonBasket: ChainBuilder =
    exec(initSession)
      .exec(StaticPages.home)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Category.productsByRandomCategory)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Product.details)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Product.addToCart(1))

  val completePurchase: ChainBuilder =
    exec(initSession)
      .exec(StaticPages.home)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Category.productsByRandomCategory)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Product.details)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Product.addToCart(1))
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Cart.view)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Cart.addProduct(2))
      .pause(LOW_PAUSE)
      .exec(Cart.removeProduct(2))
      .pause(LOW_PAUSE)
      .exec(Cart.checkout)
      .pause(LOW_PAUSE, HIGH_PAUSE)
      .exec(Customer.logout)
}