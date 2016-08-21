package com.insano10.gham.repositories

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.insano10.gham.github.entities.{User, PullRequest, UserPullRequestSummary}
import org.kohsuke.github.GitHub

import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._

class UserRepository(github: GitHub, pullRequestRepository: PullRequestRepository) {

  implicit val cache = ScalaCache(GuavaCache())

  def getUsers(repositories: List[String], monthsDataToRetrieve: Long): List[User] = {

    memoizeSync(30 minutes) {

      val pullRequests = pullRequestRepository.getPullRequests(repositories, monthsDataToRetrieve)
      buildUsers(pullRequests)
    }
  }

  def buildUsers(pullRequests: List[PullRequest]): List[User] = {

    var users: Map[String, User] = Map()

    pullRequests.foreach(pullRequest => {

      val user = getUser(pullRequest.owner, users)
      val currentOpenEndDate = pullRequest.closed match {
        case None => LocalDateTime.now()
        case Some(date) => date
      }
      val minsOpen: Long = pullRequest.created.until(currentOpenEndDate, ChronoUnit.MINUTES)

      user.pullRequestSummary.pullRequestRaised(minsOpen)
      var commentersSeen = Set[String]()

      for (comment <- pullRequest.comments) {

        if (!commentersSeen.contains(comment.owner) && !(comment.owner == pullRequest.owner)) {

          val commenterUser = getUser(comment.owner, users)
          val minsTillFirstComment: Long = pullRequest.created.until(comment.created, ChronoUnit.MINUTES)
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

    if(users.contains(username)) {
      users(username)
    } else {
      val user = github.getUser(username)
      new User(username, user.getAvatarUrl, new UserPullRequestSummary(username))
    }
  }

}
