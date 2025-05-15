package pl.training.aceshop.variant4.simulations

import io.gatling.core.Predef._

import io.gatling.core.structure.ScenarioBuilder
import scala.concurrent.duration._

object TestScenario {

  private val TEST_DURATION: FiniteDuration =
    sys.props.get("DURATION").map(_.toInt).getOrElse(60).seconds

  val defaultLoadTest: ScenarioBuilder =
    scenario("Default Load Test")
      .during(TEST_DURATION) {
        randomSwitch(
          60.0 -> exec(UserJourney.browseStore),
          30.0 -> exec(UserJourney.abandonBasket),
          10.0 -> exec(UserJourney.completePurchase)
        )
      }

  val highPurchaseLoadTest: ScenarioBuilder =
    scenario("High Purchase Load Test")
      .during(TEST_DURATION) {
        randomSwitch(
          30.0 -> exec(UserJourney.browseStore),
          30.0 -> exec(UserJourney.abandonBasket),
          40.0 -> exec(UserJourney.completePurchase)
        )
      }
}