package pl.training

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.DurationInt

class VideoGameTest extends Simulation {

  def getAllVideoGames: ChainBuilder = {
    exec(http("Get all video games")
      .get("/videogame")
      .check(status.is(200))
      .check(jsonPath("$[0].id").saveAs("gameId"))
    )
  }

  val authenticate: ChainBuilder = {
    exec(http("Authenticate")
      .post("/authenticate")
      .body(StringBody("""{"username":"admin","password":"admin"}"""))
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("jwtToken"))
    )
  }

  val createVideoGame: ChainBuilder = {
    exec(http("Create video game")
      .post("/videogame")
      .header("Authorization", "Bearer #{jwtToken}")
      .body(StringBody("""{"name":"Resident Evil 5","description":"Resident Evil 5","genre":"Action","releaseDate":"2018-01-01","rating":10}"""))
      .check(status.is(201)))
  }

  val httpProtocol: HttpProtocolBuilder = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val scn: ScenarioBuilder = scenario("Base scenario")
    .exec(getAllVideoGames)
    /*.exec(http("Get all video games")
      .get("/videogame")
      .check(status.is(200))
      .check(jsonPath("$[0].id").saveAs("gameId"))
    )*/
    .exec { session => println(session); session }
    .pause(3)
    .exec(http("Get specific game")
      .get("/videogame/#{gameId}")
      .check(status.in(200 to 299))
      .check(jsonPath("$.name").is("Resident Evil 4"))
      .check(bodyString.saveAs("residentEvilGame"))
    )
    .exec { session => println(session("residentEvilGame").as[String]); session }
    .pause(1, 5)
    .repeat(4) {
        exec(http("Get all video games")
        .get("/videogame")
        .check(status.not(404), status.not(500))
      )
      .pause(3_000.milliseconds)
    }
    .exec(authenticate)
    .exec(createVideoGame)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

}
