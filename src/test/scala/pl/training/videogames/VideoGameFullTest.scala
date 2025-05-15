package pl.training.videogames

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class VideoGameFullTest extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val USER_COUNT: Int = System.getProperty("USERS", "5").toInt
  val RAMP_DURATION: Int = System.getProperty("RAMP_DURATION", "10").toInt
  val TEST_DURATION: Int = System.getProperty("TEST_DURATION", "30").toInt

  val csvFeeder: BatchableFeederBuilder[String] = csv("data/games.csv").random

  before {
    println(s"Running test with ${USER_COUNT} users")
    println(s"Ramping users over ${RAMP_DURATION} seconds")
    println(s"Total test duration: ${TEST_DURATION} seconds")
  }

  val getAllVideoGames: ChainBuilder = {
    exec(
      http("Get all video games")
        .get("/videogame")
        .check(status.is(200))
    )
  }

  val authenticate: ChainBuilder = {
    exec(http("Authenticate")
      .post("/authenticate")
      .body(StringBody("""{"username":"admin","password":"admin"}"""))
      .check(jsonPath("$.token").saveAs("jwtToken")))
  }

  val createNewGame: ChainBuilder = {
    feed(csvFeeder)
      .exec(http("Create New Game - #{name}")
        .post("/videogame")
        .header("authorization", "Bearer #{jwtToken}")
        .body(ElFileBody("bodies/newGame.json")).asJson)
  }

  val getSingleGame: ChainBuilder = {
    exec(http("Get single game - #{name}")
      .get("/videogame/#{gameId}")
      .check(jsonPath("$.name").is("#{name}")))
  }

  val deleteGame: ChainBuilder = {
    exec(http("Delete game - #{name}")
      .delete("/videogame/#{gameId}")
      .header("authorization", "Bearer #{jwtToken}")
      .check(bodyString.is("Video game deleted")))
  }

  val scn: ScenarioBuilder = scenario("Video Game DB Final Script")
    .forever {
      exec(getAllVideoGames
        .pause(2)
        .exec(authenticate)
        .pause(2)
        .exec(createNewGame)
        .pause(2)
        .exec(getSingleGame)
        .pause(2)
        .exec(deleteGame)
      )
    }

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(USER_COUNT).during(RAMP_DURATION)
    ).protocols(httpProtocol)
  ).maxDuration(TEST_DURATION)

  after {
    println("Stress test completed")
  }

}