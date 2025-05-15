package pl.training.videogames

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.DurationInt

class VideoGameTest extends Simulation {

  val csvFeeder: BatchableFeederBuilder[String] = csv("data/games.csv").circular

  val ids: Iterator[Int] = (1 to 10).iterator
  val customFeeder: Iterator[Map[String, Int]] = Iterator.continually(Map("gameId" -> ids.next()))

  val iterateOverGames: ChainBuilder = {
    repeat(10) {
      feed(csvFeeder).exec(http("Get game: #{gameName}")
        .get("/videogame/#{gameId}")
        .check(status.in(200 to 299))
      ).pause(1)
    }
  }

  val getAllVideoGames: ChainBuilder = {
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

  val USER_COUNT: Int = System.getProperty("user.count", "1").toInt
  val RAMP_DURATION: Int = System.getProperty("ramp.duration", "5").toInt

  before {
    println("Before scenario")
  }

  after(
    println("After scenario")
  )

  // setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

  setUp(scn.inject(
    nothingFor(3.seconds),
    rampUsers(USER_COUNT).during(RAMP_DURATION)
  )).protocols(httpProtocol)

}

// mvnw gatling:test -Dgatling.simulationClass=pl.training.VideoGameTest -Duser.count=4