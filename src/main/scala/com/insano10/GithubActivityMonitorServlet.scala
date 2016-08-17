package com.insano10

import com.insano10.github.{PullRequestAnalyser, PullRequestTransformer, PullRequestFetcher}
import com.insano10.github.entities.PullRequestSummary
import com.typesafe.config.ConfigFactory
import org.json4s.{DefaultFormats, Formats}
import org.kohsuke.github.GitHub
import org.scalatra.CorsSupport
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.json._
import org.slf4j.{LoggerFactory, Logger}
import scala.collection.JavaConverters._

class GithubActivityMonitorServlet extends GithubActivityMonitorStack with JacksonJsonSupport with CorsSupport {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

  get("/user") {

    val config = ConfigFactory.load
    val github = GitHub.connectUsingOAuth(config.getString("github.oauthToken"))
    val pullRequestFetcher = new PullRequestFetcher(github)

    val foo = config.getStringList("repos").asScala.map(repo => {
      val pullRequests = pullRequestFetcher.fetch(repo)

      val pullRequestTransformer = new PullRequestTransformer()
      val transformedRequests = pullRequestTransformer.transform(pullRequests, 3)

      val pullRequestAnalyser = new PullRequestAnalyser()
      pullRequestAnalyser.analyse(transformedRequests)
    })

    foo.flatten.toList

//    "[{\"user\":\"bob\",\"totalPullRequestsRaised\":2,\"totalPullRequestsCommentedOn\":10,\"avgMinsToClose\":96,\"avgMinsToFirstComment\":361},{\"user\":\"janet\",\"totalPullRequestsRaised\":16,\"totalPullRequestsCommentedOn\":25,\"avgMinsToClose\":1646,\"avgMinsToFirstComment\":119},{\"user\":\"thomas\",\"totalPullRequestsRaised\":21,\"totalPullRequestsCommentedOn\":25,\"avgMinsToClose\":705,\"avgMinsToFirstComment\":496},{\"user\":\"kayleigh\",\"totalPullRequestsRaised\":12,\"totalPullRequestsCommentedOn\":24,\"avgMinsToClose\":30,\"avgMinsToFirstComment\":623},{\"user\":\"johnathan\",\"totalPullRequestsRaised\":18,\"totalPullRequestsCommentedOn\":10,\"avgMinsToClose\":1867,\"avgMinsToFirstComment\":81},{\"user\":\"susanna\",\"totalPullRequestsRaised\":0,\"totalPullRequestsCommentedOn\":1,\"avgMinsToClose\":0,\"avgMinsToFirstComment\":126}]"
  }

}
