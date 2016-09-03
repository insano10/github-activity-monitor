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

    if(typesafeConfig.hasPath("gocd")) {

      val gocdClient = new GoCDClient(system, typesafeConfig.getString("gocd.baseUrl"),
                                              typesafeConfig.getString("gocd.username"),
                                              typesafeConfig.getString("gocd.password"))
      val goCDDeploymentStatusRetriever = new GoCDDeploymentStatusRetriever(gocdClient, typesafeConfig)

      repoRepository.setDeploymentStatusRetriever(goCDDeploymentStatusRetriever)
    }
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

    new AsyncResult() {val is =
      repoRepository.getRepositorySummaries(repoList, daysDataToRetrieve)
    }

    //todo: sort by last push time (make field a proper date and format in client
//    "[" +
//      "{\"name\": \"repository-two\",\"pullRequests\": [{\"repositoryName\": \"repository-two\",\"repositoryFullName\": \"insano10/repository-two\",\"owner\": \"insano10\",\"title\": \"Awesome pull request2\",\"created\": \"2016-08-15T12:00:00Z\",\"closed\":  \"2016-08-15T12:05:00Z\", \"comments\": []}],\"lastPushTime\": \"Mon 2 April 2016\",\"mostRecentCommit\": {\"owner\": \"insano10\",\"avatarUrl\": \"https://avatars0.githubusercontent.com/u/7420159?v=3&s=460\",\"url\": \"http://google.com\",\"message\": \"commit this stuff2\"},\"hasOpenPullRequests\": false,\"needsDeployment\": false}," +
//      "{\"name\": \"repository-four\",\"pullRequests\": [{\"repositoryName\": \"repository-four\",\"repositoryFullName\": \"insano10/repository-four\",\"owner\": \"insano10\",\"title\": \"Awesome pull request2\",\"created\": \"2016-08-15T12:00:00Z\",\"closed\":  \"2016-08-15T12:05:00Z\", \"comments\": []}],\"lastPushTime\": \"Mon 5 April 2016\",\"mostRecentCommit\": {\"owner\": \"insano10\",\"avatarUrl\": \"https://avatars0.githubusercontent.com/u/7420159?v=3&s=460\",\"url\": \"http://google.com\",\"message\": \"commit this stuff2\"},\"hasOpenPullRequests\": false,\"needsDeployment\": false}," +
//      "{\"name\": \"repository-one\",\"pullRequests\": [{\"repositoryName\": \"repository-one\",\"repositoryFullName\": \"insano10/repository-one\",\"owner\": \"insano10\",\"title\": \"Awesome pull request\",\"created\": \"2016-08-15T12:00:00Z\",\"closed\":  \"2016-08-15T12:05:00Z\", \"comments\": []}],\"lastPushTime\": \"Mon 2 April 2016\",\"mostRecentCommit\": {\"owner\": \"insano10\",\"avatarUrl\": \"https://avatars0.githubusercontent.com/u/7420159?v=3&s=460\",\"url\": \"http://google.com\",\"message\": \"commit this stuff\"},\"hasOpenPullRequests\": false,\"needsDeployment\": true}, " +
//      "{\"name\": \"repository-three\",\"pullRequests\": [{\"repositoryName\": \"repository-three\",\"repositoryFullName\": \"insano10/repository-three\",\"owner\": \"insano10\",\"title\": \"Awesome pull request\",\"created\": \"2016-08-15T12:00:00Z\", \"comments\": []}],\"lastPushTime\": \"Mon 2 April 2016\",\"mostRecentCommit\": {\"owner\": \"insano10\",\"avatarUrl\": \"https://avatars0.githubusercontent.com/u/7420159?v=3&s=460\",\"url\": \"http://google.com\",\"message\": \"commit this stuff\"},\"hasOpenPullRequests\": true,\"needsDeployment\": false}" +
//      "]"
    
  }

  get("/config") {

    appConfig
  }

}
