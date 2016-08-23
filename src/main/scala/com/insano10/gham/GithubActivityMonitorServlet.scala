package com.insano10.gham

import _root_.akka.actor.ActorSystem
import com.insano10.gham.entities.AppConfig
import com.insano10.gham.gocd.GoCDClient
import com.insano10.gham.repositories.{RepositoryRepository, UserRepository, PullRequestRepository}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.json4s.{DefaultFormats, Formats}
import org.kohsuke.github.GitHub
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport

import scala.collection.JavaConverters._

class GithubActivityMonitorServlet(val system: ActorSystem) extends GithubActivityMonitorStack
  with JacksonJsonSupport
  with CorsSupport
  with StrictLogging {

  protected implicit val jsonFormats: Formats = DefaultFormats

  private val typesafeConfig = ConfigFactory.load
  private val monthsDataToRetrieve = typesafeConfig.getInt("monthsDataToRetrieve")
  private val organisation = typesafeConfig.getString("organisation")
  private val appConfig = new AppConfig(organisation, monthsDataToRetrieve)
  private val github = GitHub.connectUsingOAuth(typesafeConfig.getString("github.oauthToken"))
  private val repoList = typesafeConfig.getStringList("repos").asScala.toList

  private val pullRequestRepository = new PullRequestRepository(github)
  private val userRepository = new UserRepository(github, pullRequestRepository)
  private val repoRepository = new RepositoryRepository(github, pullRequestRepository)
  private val gocdClient = new GoCDClient(system, typesafeConfig.getString("gocd.baseUrl"),
                                                  typesafeConfig.getString("gocd.username"),
                                                  typesafeConfig.getString("gocd.password"))

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

    userRepository.getUsers(repoList, monthsDataToRetrieve)
  }

  get("/repository") {

    repoRepository.getRepositorySummaries(repoList, monthsDataToRetrieve)
  }

  get("/config") {

    appConfig
  }

}
