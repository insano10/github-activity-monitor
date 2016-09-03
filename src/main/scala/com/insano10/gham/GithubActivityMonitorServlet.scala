package com.insano10.gham

import _root_.akka.actor.ActorSystem
import com.insano10.gham.github.entities.AppConfig
import com.insano10.gham.github.repositories.{PullRequestRepository, RepositoryRepository, UserRepository}
import com.insano10.gham.gocd.{GoCDClient, GoCDDeploymentStatusRetriever}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.json4s.{DefaultFormats, Formats}
import org.kohsuke.github.GitHub
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class GithubActivityMonitorServlet(val system: ActorSystem) extends GithubActivityMonitorStack
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

  private val pullRequestRepository = new PullRequestRepository(github)
  private val userRepository = new UserRepository(github, pullRequestRepository)
  private val repoRepository = new RepositoryRepository(github, pullRequestRepository)

  override def initialize(config: ConfigT): Unit = {
    super.initialize(config)

    if (typesafeConfig.hasPath("gocd")) {

      val gocdUrl = typesafeConfig.getString("gocd.baseUrl")
      val gocdClient = new GoCDClient(system, gocdUrl,
                                              typesafeConfig.getString("gocd.username"),
                                              typesafeConfig.getString("gocd.password"))
      val goCDDeploymentStatusRetriever = new GoCDDeploymentStatusRetriever(gocdClient, typesafeConfig, gocdUrl)

      repoRepository.setDeploymentStatusRetriever(goCDDeploymentStatusRetriever)
    }

    primeCaches()
  }

  def primeCaches() = {
    system.scheduler.schedule(0 minutes, 1 minute)(repoRepository.getRepositorySummaries(repoList, daysDataToRetrieve))
  }

  before() {
    contentType = formats("json")
  }

  errorHandler = {
    case t =>
      logger.error("Error in route", t)
      throw t
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

  get("/user") {

    userRepository.getUsers(repoList, daysDataToRetrieve)
  }

  get("/repository") {

    new AsyncResult() {
      val is =
        repoRepository.getRepositorySummaries(repoList, daysDataToRetrieve)
    }
  }

  get("/config") {

    appConfig
  }

}
