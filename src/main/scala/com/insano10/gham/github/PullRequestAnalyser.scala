package com.insano10.gham.github

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.insano10.gham.github.entities.{UserSummary, PullRequest}

class PullRequestAnalyser {

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
