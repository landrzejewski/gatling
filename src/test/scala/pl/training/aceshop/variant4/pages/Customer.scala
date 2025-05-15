package pl.training.aceshop.variant4.pages

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.util.Random

object Customer {

  private val loginFeeder = Iterator.continually {
    val userId = "user" + (Random.nextInt(3) + 1)
    Map(
      "userId" -> userId,
      "password" -> "pass"
    )
  }

  val login: ChainBuilder = feed(loginFeeder)
    .exec(
      http("Login User")
        .post("/login")
        .formParam("_csrf", "#{csrfToken}")
        .formParam("username", "#{userId}")
        .formParam("password", "#{password}")
        .check(css("#_csrf", "content").saveAs("csrfTokenLoggedIn"))
    )

  val logout: ChainBuilder = randomSwitch(
    10.0 -> exec(
      http("Logout")
        .post("/logout")
        .formParam("_csrf", "${csrfTokenLoggedIn}")
        .check(css("#LoginLink").is("Login"))
    )
  )

}
