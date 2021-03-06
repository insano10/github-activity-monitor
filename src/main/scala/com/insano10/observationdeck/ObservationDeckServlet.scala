package com.insano10.observationdeck

import _root_.akka.actor.ActorSystem
import com.insano10.observationdeck.github.entities.AppConfig
import com.insano10.observationdeck.github.repositories.{PullRequestRepository, RepositoryRepository, UserRepository}
import com.insano10.observationdeck.gocd.history.{ReleaseHistoryRepository, ReleaseHistoryRetriever}
import com.insano10.observationdeck.gocd.{GoCDClient, GoCDDeploymentStatusRetriever}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.json4s.{DefaultFormats, Formats}
import org.kohsuke.github.GitHub
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ObservationDeckServlet(val system: ActorSystem) extends ObservationDeckStack
  with JacksonJsonSupport
  with CorsSupport
  with FutureSupport
  with StrictLogging {

  protected implicit def executor: ExecutionContext = system.dispatcher
  protected implicit val jsonFormats: Formats = DefaultFormats

  private val typesafeConfig = ConfigFactory.load
  private val daysDataToRetrieve = typesafeConfig.getInt("daysDataToRetrieve")
  private val boardName = typesafeConfig.getString("boardName")
  private val appConfig = new AppConfig(boardName, daysDataToRetrieve)
  private val github = GitHub.connectUsingOAuth(typesafeConfig.getString("github.oauthToken"))
  private val repoList = typesafeConfig.getStringList("repos").asScala.toList
  private val ignoredCommitters = typesafeConfig.getStringList("ignoreCommitsFrom").asScala.toList

  private val pullRequestRepository = new PullRequestRepository()
  private val userRepository = new UserRepository(github, pullRequestRepository)
  private val repoRepository = new RepositoryRepository(github, pullRequestRepository)
  private val releaseHistoryRepository = new ReleaseHistoryRepository()

  override def initialize(config: ConfigT): Unit = {
    super.initialize(config)

    if (typesafeConfig.hasPath("gocd")) {

      val gocdUrl = typesafeConfig.getString("gocd.baseUrl")
      val gocdClient = new GoCDClient(system, gocdUrl,
        typesafeConfig.getString("gocd.username"),
        typesafeConfig.getString("gocd.password"),
        ignoredCommitters)
      val goCDDeploymentStatusRetriever = new GoCDDeploymentStatusRetriever(gocdClient, typesafeConfig, gocdUrl, github)

      repoRepository.setDeploymentStatusRetriever(goCDDeploymentStatusRetriever)

      val releaseHistoryRetriever = new ReleaseHistoryRetriever(gocdClient, typesafeConfig, gocdUrl, daysDataToRetrieve)
      releaseHistoryRepository.initialise(releaseHistoryRetriever)
    }

    primeCaches()
  }

  def primeCaches() = {

    logger.info("Starting cache poker")

    val task = new Runnable {
      def run() {
        repoRepository.getRepositorySummaries(repoList, daysDataToRetrieve)
      }
    }
    system.scheduler.schedule(
      initialDelay = 0 minutes,
      interval = 1 minute,
      runnable = task)
  }

  errorHandler = {
    case t =>
      logger.error("Error in route", t)
      throw t
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

  get("/api/user") {

    userRepository.getUsers(repoList, daysDataToRetrieve)
  }

  get("/api/repository") {

    new AsyncResult() {
      val is =
        repoRepository.getRepositorySummaries(repoList, daysDataToRetrieve)
    }
  }

  get("/api/config") {

    appConfig
  }

  get("/api/history/release") {

    new AsyncResult() {
      val is = releaseHistoryRepository.getReleaseHistory
    }
  }

}
