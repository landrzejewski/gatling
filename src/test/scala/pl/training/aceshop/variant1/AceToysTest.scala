package pl.training.aceshop.variant1

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

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
    .exec(
      http("Load Home Page")
        .get("/")
        .check(status.is(200))
        .check(status.not(404), status.not(405))
        .check(substring("<title>Ace Toys Online Shop</title>"))
        .check(css("#_csrf", "content").saveAs("csrfToken"))
    )
    .pause(2)
    .exec(
      http("Load Our Story Page")
        .get("/our-story")
        .check(regex("was founded online in \\d{4}"))
    )
    .pause(2)
    .exec(http("Load Get In Touch").get("/get-in-touch"))
    .pause(2)
    .exec(http("Load Products List Page - All Products").get("/category/all"))
    .pause(2)
    .exec(http("Load Page 1").get("/category/all?page=1"))
    .pause(2)
    .exec(http("Load Page 2").get("/category/all?page=2"))
    .pause(2)
    .exec(http("Load Product Detail - Darts Board").get("/product/darts-board"))
    .pause(2)
    .exec(http("Add to Cart: 19").get("/cart/add/19"))
    .pause(2)
    .exec(http("Load Babies Toys").get("/category/babies-toys"))
    .pause(2)
    .exec(http("Add to Cart: 4").get("/cart/add/4"))
    .pause(2)
    .exec(http("Add to Cart Again: 4").get("/cart/add/4"))
    .pause(2)
    .exec(http("View Cart").get("/cart/view"))
    .pause(2)
    .exec(
      http("Login User")
        .post("/login")
        .formParam("_csrf", "${csrfToken}")
        .formParam("username", "user1")
        .formParam("password", "pass")
        .check(css("#_csrf", "content").saveAs("csrfTokenLoggedIn"))
    )
    .pause(2)
    .exec(http("Increase Qty - 19").get("/cart/add/19?cartPage=true"))
    .pause(2)
    .exec(http("Increase Qty Again - 19").get("/cart/add/19?cartPage=true"))
    .pause(2)
    .exec(http("Subtract Qty - 19").get("/cart/subtract/19"))
    .pause(2)
    .exec(
      http("Checkout")
        .get("/cart/checkout")
        .check(substring("Your products are on their way to you now!!"))
    )
    .pause(2)
    .exec(
      http("Logout")
        .post("/logout")
        .formParam("_csrf", "${csrfTokenLoggedIn}")
    )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
