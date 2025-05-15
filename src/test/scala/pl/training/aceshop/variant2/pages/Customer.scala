package pl.training.aceshop.variant2.pages

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Customer {

  val login: ChainBuilder = exec(
    http("Login User")
      .post("/login")
      .formParam("_csrf", "${csrfToken}")
      .formParam("username", "user1")
      .formParam("password", "pass")
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
