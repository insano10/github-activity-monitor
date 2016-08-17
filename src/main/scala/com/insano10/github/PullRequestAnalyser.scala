package com.insano10.github

import java.time.temporal.ChronoUnit

import com.insano10.github.entities.{PullRequestSummary, PullRequest}

class PullRequestAnalyser {

  def analyse(pullRequests: List[PullRequest]): List[PullRequestSummary] = {

    var summaries: Map[String, PullRequestSummary] = Map()

    pullRequests.foreach(pullRequest => {

      val summary = getSummary(pullRequest.owner, summaries)
      val minsOpen: Long = pullRequest.created.until(pullRequest.closed, ChronoUnit.MINUTES)

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

  private def getSummary(user: String, summaries: Map[String, PullRequestSummary]) = {

    summaries.getOrElse(user, new PullRequestSummary(user))
  }
}
