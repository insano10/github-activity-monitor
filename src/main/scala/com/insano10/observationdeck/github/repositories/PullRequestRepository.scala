package com.insano10.observationdeck.github.repositories

import java.io.IOException
import java.util.Date

import com.insano10.observationdeck.github.entities.GithubEntities.{Comment, PullRequest}
import com.typesafe.scalalogging.StrictLogging
import org.kohsuke.github._

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._

class PullRequestRepository(github: GitHub) extends StrictLogging {

  implicit val cache = ScalaCache(GuavaCache())

  def getPullRequests(repository: GHRepository, daysDataToRetrieve: Int): List[PullRequest] = {

    memoizeSync(10 minutes) {

      val pullRequests = fetchPullRequests(repository)
      transform(pullRequests, daysDataToRetrieve)
    }
  }

  def fetchPullRequests(repository: GHRepository): PagedIterable[GHPullRequest] = {

    try {
      repository.queryPullRequests.base("master").direction(GHDirection.DESC).state(GHIssueState.ALL).list.withPageSize(10)
    }
    catch {
      case e: IOException => throw new RuntimeException("Failed to get pull requests from repository " + repository.getFullName, e)

    }
  }

  def transform(pullRequests: PagedIterable[GHPullRequest], daysRetrieved: Int): List[PullRequest] = {

    val minTime = System.currentTimeMillis() - (daysRetrieved * 24 * 60 * 60 * 1000L)

    try {

      pullRequests.iterator().asScala.
        takeWhile(pr => pr.getCreatedAt.getTime > minTime).
        map(pr => {
          val pullRequest: PullRequest = new PullRequest(pr.getRepository.getName,
            pr.getRepository.getFullName,
            pr.getUser.getLogin,
            pr.getTitle,
            pr.getCreatedAt.getTime,
            dateToOptMillis(pr.getClosedAt))

          for (comment <- pr.getComments.asScala) {
            pullRequest.addComment(new Comment(comment.getUser.getLogin, comment.getUpdatedAt.getTime))
          }
          pullRequest
        }).
        toList
    }
    catch {
      case e: IOException => throw new RuntimeException("Failed to transform pull request", e)
    }
  }

  def dateToOptMillis(date: Date) = date match {
    case null => None
    case x => Some(x.getTime)
  }

}
