package com.insano10.gham.entities

import java.time.LocalDateTime

object GithubEntities {

  case class Comment(val owner: String, val created: LocalDateTime)

  case class PullRequest(val repositoryName: String,
                         val repositoryFullName: String,
                         val owner: String,
                         val title: String,
                         val created: LocalDateTime,
                         val closed: Option[LocalDateTime],
                         var comments: List[Comment] = List()) {

    def addComment(comment: Comment): Unit = {
      comments = comment :: comments
    }
  }

  case class Commit(val owner: String, val avatarUrl: String, val url: String, val message: String)

  case class User(val username: String, val avatarUrl: String, val pullRequestSummary: UserPullRequestSummary)

  case class UserPullRequestSummary(val user: String,
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
      totalPullRequestsCommentedOn += 1
    }
  }

  case class RepositorySummary(val name: String,
                               val pullRequests: List[PullRequest],
                               val lastPushTime: String,
                               val mostRecentCommit: Commit,
                               val needsDeployment: Boolean)

}
