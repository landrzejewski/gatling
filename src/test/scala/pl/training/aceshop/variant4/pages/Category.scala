package pl.training.aceshop.variant4.pages

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Category {

  val categoryFeeder: BatchableFeederBuilder[String] = csv("data/categoryDetails.csv").circular

  val products: ChainBuilder = exec(
    http("Load Products List Page - All Products")
      .get("/category/all")
  )

  def productsPage(pageNumber: Int): ChainBuilder = exec(
    http(s"Load Page ${pageNumber}")
      .get(s"/category/all?page=${pageNumber}")
  )

  val productsPage: ChainBuilder = exec(
    http("Load Page #{currentPage}")
      .get("/category/all?page=#{currentPage}")
  )

  def productsByCategory(categoryName: String): ChainBuilder = exec(
    http(s"Load Page ${categoryName}")
      .get(s"/category/${categoryName}"
      )
  )

  val productsByRandomCategory: ChainBuilder = feed(categoryFeeder)
    .exec(http("Load Page #{categoryName}")
      .get("/category/#{categorySlug}")
      .check(css("#CategoryName").is("#{categoryName}"))
    )

  val iterateOverPages: ChainBuilder = exec { session =>
    val currentPage = 0
    val totalPages = session("categoryPages").as[Int]
    session
      .set("currentPage", currentPage)
      .set("hasMorePages", currentPage < totalPages)
  }
    .asLongAs(session => session("hasMorePages").as[Boolean]) {
      exec(Category.productsPage)
        .exec { session =>
          val currentPage = session("currentPage").as[Int] + 1
          val totalPages = session("categoryPages").as[Int]
          session
            .set("currentPage", currentPage)
            .set("hasMorePages", currentPage < totalPages)
        }
    }

}
