package pl.training.aceshop.variant4

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import pl.training.aceshop.variant4.simulations.TestPopulation

class AceToysTest extends Simulation {

  private val TEST_TYPE = sys.props.getOrElse("TEST_TYPE", "INSTANT_USERS")

  private val httpProtocol = http
    .baseUrl("https://acetoys.uk/")
    .inferHtmlResources(
      AllowList(),
      DenyList(
        """.*\.js""",
        """.*\.css""",
        """.*\.gif""",
        """.*\.jpeg""",
        """.*\.jpg""",
        """.*\.ico""",
        """.*\.woff""",
        """.*\.woff2""",
        """.*\.(t|o)tf""",
        """.*\.png""",
        """.*detectportal\.firefox\.com.*"""
      )
    )
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-GB,en;q=0.9")

  private val population = TEST_TYPE match {
    case "INSTANT_USERS"    => TestPopulation.instantUsers
    case "RAMP_USERS"       => TestPopulation.rampUsers
    case "COMPLEX_INJECTION"=> TestPopulation.complexInjection
    case "CLOSED_MODEL"     => TestPopulation.closedModel
    case _                  => TestPopulation.instantUsers
  }

  setUp(population).protocols(httpProtocol)
}