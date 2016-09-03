package com.insano10.gham.entities

object GithubEntities {

  case class Comment(val owner: String, val createdTimeMs: Long)

  case class PullRequest(val repositoryName: String,
                         val repositoryFullName: String,
                         val owner: String,
                         val title: String,
                         val createdTimeMs: Long,
                         val closedTimeMs: Option[Long],
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
                               val url: String,
                               val pullRequests: List[PullRequest],
                               val lastPushTimeMs: Long,
                               val mostRecentCommit: Commit,
                               val openPullRequests: Int,
                               val needsDeployment: Boolean,
                               val deploymentUrl: String)

}
