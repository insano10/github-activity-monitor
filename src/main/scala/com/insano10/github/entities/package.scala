package com.insano10.github

import java.time.{LocalDate, LocalDateTime}

package object entities {

  case class Comment(val owner: String, val created: LocalDateTime)

  case class PullRequest(val owner: String, val title: String, val created: LocalDateTime, val closed: LocalDateTime, var comments: List[Comment] = List()) {

    def addComment(comment: Comment): Unit = {
      comments = comment::comments
    }
  }

  case class PullRequestSummary(val user: String,
                                var totalPullRequestsRaised: Integer = 0,
                                var totalPullRequestsCommentedOn: Integer = 0,
                                var avgMinsToClose: Long = 0,
                                var avgMinsToFirstComment: Long = 0) {

    def pullRequestRaised(minsOpen: Long): Unit = {
      avgMinsToClose = ((avgMinsToClose * totalPullRequestsRaised) + minsOpen) / (totalPullRequestsRaised + 1)
      totalPullRequestsRaised += 1
    }

    def pullRequestCommentedOn(minsTillFirstComment: Long): Unit = {
      avgMinsToFirstComment = ((avgMinsToFirstComment * totalPullRequestsCommentedOn) + minsTillFirstComment) / (totalPullRequestsCommentedOn + 1)
      totalPullRequestsCommentedOn +=1
    }
  }
}