package pl.training.aceshop.variant2.pages

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Category {

  val products: ChainBuilder = exec(
    http("Load Products List Page - All Products")
      .get("/category/all")
  )

  def productsPage(pageNumber :Int): ChainBuilder = exec(
    http(s"Load Page ${pageNumber}")
      .get(s"/category/all?page=${pageNumber}")
      .check(css(".page-item.active").is(s"${pageNumber}"))
  )

  def productsByCategory(categoryName :String): ChainBuilder = exec(
    http(s"Load Page ${categoryName}")
      .get(s"/category/${categoryName}"
      )
  )

}
