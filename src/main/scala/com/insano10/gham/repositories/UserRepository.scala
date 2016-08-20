package com.insano10.gham.repositories

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.insano10.gham.github.entities.{PullRequest, UserSummary}

import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._

class UserRepository(pullRequestRepository: PullRequestRepository) {

  implicit val cache = ScalaCache(GuavaCache())

  def getUserSummaries(repositories: List[String], monthsDataToRetrieve: Long): List[UserSummary] = {

    memoizeSync(30 minutes) {

      val pullRequests = pullRequestRepository.getPullRequests(repositories, monthsDataToRetrieve)
      buildUserSummaries(pullRequests)
    }
  }

  def buildUserSummaries(pullRequests: List[PullRequest]): List[UserSummary] = {

    var summaries: Map[String, UserSummary] = Map()

    pullRequests.foreach(pullRequest => {

      val summary = getSummary(pullRequest.owner, summaries)
      val currentOpenEndDate = pullRequest.closed match {
        case None => LocalDateTime.now()
        case Some(date) => date
      }
      val minsOpen: Long = pullRequest.created.until(currentOpenEndDate, ChronoUnit.MINUTES)

      summary.pullRequestRaised(minsOpen)
      var commentersSeen = Set[String]()

      for (comment <- pullRequest.comments) {

        if (!commentersSeen.contains(comment.owner) && !(comment.owner == pullRequest.owner)) {

          val commenterSummary = getSummary(comment.owner, summaries)
          val minsTillFirstComment: Long = pullRequest.created.until(comment.created, ChronoUnit.MINUTES)
          commenterSummary.pullRequestCommentedOn(minsTillFirstComment)
          commentersSeen += comment.owner
          summaries = summaries.updated(comment.owner, commenterSummary)
        }
      }
      summaries = summaries.updated(pullRequest.owner, summary)
    })

    summaries.values.toList
  }

  private def getSummary(user: String, summaries: Map[String, UserSummary]) = summaries.getOrElse(user, new UserSummary(user))

}
