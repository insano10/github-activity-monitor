package com.insano10

import com.typesafe.scalalogging.StrictLogging
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport

class GithubActivityMonitorServlet extends GithubActivityMonitorStack
  with JacksonJsonSupport
  with CorsSupport
  with StrictLogging {

  protected implicit val jsonFormats: Formats = DefaultFormats

  private val userRepository = new UserRepository()

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

    userRepository.getUsers

//    "[{\"user\":\"bob\",\"totalPullRequestsRaised\":2,\"totalPullRequestsCommentedOn\":10,\"avgMinsToClose\":96,\"avgMinsToFirstComment\":361},{\"user\":\"janet\",\"totalPullRequestsRaised\":16,\"totalPullRequestsCommentedOn\":25,\"avgMinsToClose\":1646,\"avgMinsToFirstComment\":119},{\"user\":\"thomas\",\"totalPullRequestsRaised\":21,\"totalPullRequestsCommentedOn\":25,\"avgMinsToClose\":705,\"avgMinsToFirstComment\":496},{\"user\":\"kayleigh\",\"totalPullRequestsRaised\":12,\"totalPullRequestsCommentedOn\":24,\"avgMinsToClose\":30,\"avgMinsToFirstComment\":623},{\"user\":\"johnathan\",\"totalPullRequestsRaised\":18,\"totalPullRequestsCommentedOn\":10,\"avgMinsToClose\":1867,\"avgMinsToFirstComment\":81},{\"user\":\"susanna\",\"totalPullRequestsRaised\":0,\"totalPullRequestsCommentedOn\":1,\"avgMinsToClose\":0,\"avgMinsToFirstComment\":126}]"
  }

}
