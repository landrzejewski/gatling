package pl.training.aceshop.variant2

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import pl.training.aceshop.variant2.pages.Cart.view
import pl.training.aceshop.variant2.pages.Category.{products, productsByCategory, productsPage}
import pl.training.aceshop.variant2.pages.{Cart, Category, Customer, Product, StaticPages}
import pl.training.aceshop.variant2.pages.Product.{addToCart, details}
import pl.training.aceshop.variant2.pages.StaticPages.{getInTouch, home, ourStory}

class AceToysTest extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://acetoys.uk")
    .inferHtmlResources(
      AllowList(),
      DenyList(
        """.*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""",
        """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""",
        """.*detectportal\.firefox\.com.*"""
      )
    )
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-GB,en;q=0.9")

  val scn: ScenarioBuilder = scenario("AceToysSimulation")
    .exec(StaticPages.home)
    .pause(2)
    .exec(StaticPages.ourStory)
    .pause(2)
    .exec(StaticPages.getInTouch)
    .pause(2)
    .exec(Category.products)
    .pause(2)
    .exec(Category.productsPage(1))
    .pause(2)
    .exec(Category.productsPage(2))
    .pause(2)
    .exec(Product.details("darts-board"))
    .pause(2)
    .exec(Product.addToCart(19))
    .pause(2)
    .exec(Category.productsByCategory("babies-toys"))
    .pause(2)
    .exec(Product.addToCart(4))
    .pause(2)
    .exec(Product.addToCart(4))
    .pause(2)
    .exec(Cart.view)
    .pause(2)
    .exec(Customer.login)
    .pause(2)
    .exec(Cart.addProduct(19))
    .pause(2)
    .exec(Cart.addProduct(19))
    .pause(2)
    .exec(Cart.removeProduct(19))
    .pause(2)
    .exec(Cart.checkout)
    .pause(2)
    .exec(Customer.logout)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
