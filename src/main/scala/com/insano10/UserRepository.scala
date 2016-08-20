package com.insano10

import com.insano10.github.entities.UserSummary
import com.insano10.github.{PullRequestAnalyser, PullRequestFetcher, PullRequestTransformer}
import com.typesafe.config.ConfigFactory
import org.kohsuke.github.GitHub

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scalacache.guava.GuavaCache
import scalacache.{ScalaCache, _}
import scalacache.memoization._

class UserRepository() {

  implicit val cache = ScalaCache(GuavaCache())

  def getUsers: List[UserSummary] =

    memoizeSync(30 minutes) {

      val config = ConfigFactory.load
      val github = GitHub.connectUsingOAuth(config.getString("github.oauthToken"))

      val pullRequestFetcher = new PullRequestFetcher(github)
      val pullRequestTransformer = new PullRequestTransformer()
      val pullRequestAnalyser = new PullRequestAnalyser()

      config.getStringList("repos").asScala.flatMap(repo => {

        val pullRequests = pullRequestFetcher.fetch(repo)
        val transformedRequests = pullRequestTransformer.transform(pullRequests, 1)

        pullRequestAnalyser.buildUserSummaries(transformedRequests)
      }).toList
    }

}
