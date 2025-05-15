package pl.training.aceshop.variant4

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._


object Session {

  val initSession: ChainBuilder = exec(flushCookieJar)
    .exec(_.set("pageNumber", 1))
    .exec(_.set("isAuthenticated", false))
    .exec(_.set("itemsInCart", 0))
    .exec(_.set("cartTotalBalance", 0.0))

  val increaseItemsCount: ChainBuilder = exec { session =>
    val itemsInCart = session("itemsInCart").as[Int]
    session.set("itemsInCart", itemsInCart + 1)
  }

  val decreaseItemsCount: ChainBuilder = exec { session =>
    val itemsInCart = session("itemsInCart").as[Int]
    session.set("itemsInCart", itemsInCart - 1)
  }

  val increaseCartTotalBalance: ChainBuilder = exec { session =>
    val cartTotalBalance = session("cartTotalBalance").as[Double]
    val itemPrice = session("itemPrice").as[Double]
    session.set("cartTotalBalance", cartTotalBalance + itemPrice)
  }

  val decreaseCartTotalBalance: ChainBuilder = exec { session =>
    val cartTotalBalance = session("cartTotalBalance").as[Double]
    val itemPrice = session("itemPrice").as[Double]
    session.set("cartTotalBalance", cartTotalBalance - itemPrice)
  }

  def setAuthenticated(value: Boolean): ChainBuilder = exec(_.set("isAuthenticated", value))

}
