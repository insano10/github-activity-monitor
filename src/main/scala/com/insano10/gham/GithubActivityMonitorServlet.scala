package com.insano10.gham

import com.insano10.gham.repositories.{PullRequestRepository, RepositoryRepository, UserRepository}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.json4s.{DefaultFormats, Formats}
import org.kohsuke.github.GitHub
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport

import scala.collection.JavaConverters._

class GithubActivityMonitorServlet extends GithubActivityMonitorStack
  with JacksonJsonSupport
  with CorsSupport
  with StrictLogging {

  protected implicit val jsonFormats: Formats = DefaultFormats

  private val appConfig = ConfigFactory.load
  private val github = GitHub.connectUsingOAuth(appConfig.getString("github.oauthToken"))
  private val repoList = appConfig.getStringList("repos").asScala.toList
  private val monthsDataToRetrieve = 1

  private val pullRequestRepository = new PullRequestRepository(github)
  private val userRepository = new UserRepository(pullRequestRepository)
  private val repoRepository = new RepositoryRepository(github, pullRequestRepository)

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

    userRepository.getUserSummaries(repoList, monthsDataToRetrieve)
  }

  get("/repository") {

    repoRepository.getRepositorySummaries(repoList, monthsDataToRetrieve)
  }

}
