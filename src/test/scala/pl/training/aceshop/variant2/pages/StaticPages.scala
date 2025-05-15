package pl.training.aceshop.variant2.pages

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object StaticPages {

  val home: ChainBuilder = exec(
    http("Load Home Page")
      .get("/")
      .check(status.is(200))
      .check(status.not(404), status.not(405))
      .check(substring("<title>Ace Toys Online Shop</title>"))
      .check(css("#_csrf", "content").saveAs("csrfToken"))
  )

  val ourStory: ChainBuilder = exec(
    http("Load Our Story Page")
      .get("/our-story")
      .check(regex("was founded online in \\d{4}"))
  )

  val getInTouch: ChainBuilder = exec(
    http("Load Get In Touch")
      .get("/get-in-touch")
      .check(substring("as we are not actually a real store!"))
  )

}
