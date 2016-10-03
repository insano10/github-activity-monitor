package com.insano10.observationdeck.github.repositories

import com.insano10.observationdeck.github.entities.GithubEntities.{UserPullRequestSummary, PullRequest, User}
import org.kohsuke.github.GitHub

import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._

class UserRepository(github: GitHub, pullRequestRepository: PullRequestRepository) {

  implicit val cache = ScalaCache(GuavaCache())

  def getUsers(repositories: List[String], daysDataToRetrieve: Int): List[User] = {

    memoizeSync(30 minutes) {

      val pullRequests = repositories.flatMap(repoFullName => {
        val repo = github.getRepository(repoFullName)
        pullRequestRepository.getPullRequests(repo, daysDataToRetrieve)
      })

      buildUsers(pullRequests)
    }
  }

  def buildUsers(pullRequests: List[PullRequest]): List[User] = {

    var users: Map[String, User] = Map()

    pullRequests.foreach(pullRequest => {

      val user = getUser(pullRequest.owner, users)
      val currentOpenEndTimeMs = pullRequest.closedTimeMs match {
        case None => System.currentTimeMillis()
        case Some(date) => date
      }
      val minsOpen = (currentOpenEndTimeMs - pullRequest.createdTimeMs) * 60 * 60

      user.pullRequestSummary.pullRequestRaised(minsOpen)
      var commentersSeen = Set[String]()

      for (comment <- pullRequest.comments) {

        if (!commentersSeen.contains(comment.owner) && !(comment.owner == pullRequest.owner)) {

          val commenterUser = getUser(comment.owner, users)
          val minsTillFirstComment: Long = (comment.createdTimeMs - pullRequest.createdTimeMs) * 60 * 60

          commenterUser.pullRequestSummary.pullRequestCommentedOn(minsTillFirstComment)
          commentersSeen += comment.owner
          users = users.updated(comment.owner, commenterUser)
        }
      }
      users = users.updated(pullRequest.owner, user)
    })

    users.values.toList
  }

  private def getUser(username: String, users: Map[String, User]): User = {

    if (users.contains(username)) {
      users(username)
    } else {
      val user = github.getUser(username)
      new User(username, user.getAvatarUrl, new UserPullRequestSummary(username))
    }
  }

}
