package pl.training.aceshop.variant4.simulations

import io.gatling.core.Predef._
import scala.concurrent.duration._
import io.gatling.core.structure.PopulationBuilder

object TestPopulation {

  private val USER_COUNT: Int = sys.props.get("USERS").map(_.toInt).getOrElse(5)
  private val RAMP_DURATION: FiniteDuration = sys.props.get("RAMP_DURATION").map(_.toInt).getOrElse(10).seconds

  val instantUsers: PopulationBuilder =
    TestScenario.defaultLoadTest.inject(
      nothingFor(5.seconds),
      atOnceUsers(USER_COUNT)
    )

  val rampUsers: PopulationBuilder =
    TestScenario.defaultLoadTest.inject(
      nothingFor(5.seconds),
      rampUsersPerSec(USER_COUNT).to(20).during(RAMP_DURATION)
    )

  val complexInjection: PopulationBuilder =
    TestScenario.defaultLoadTest.inject(
      constantUsersPerSec(10).during(20.seconds).randomized,
      rampUsersPerSec(10).to(20).during(20.seconds).randomized
    )

  val closedModel: PopulationBuilder =
    TestScenario.highPurchaseLoadTest.inject(
      constantConcurrentUsers(10).during(20.seconds),
      rampConcurrentUsers(10).to(20).during(20.seconds)
    )

}
