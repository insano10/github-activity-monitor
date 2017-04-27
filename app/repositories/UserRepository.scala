package repositories

import models.GithubEntities.{UserPullRequestSummary, PullRequest, User}
import org.kohsuke.github.GitHub
import play.api.cache.CacheApi

import scala.concurrent.duration._

class UserRepository(github: GitHub, pullRequestRepository: PullRequestRepository, cache: CacheApi) {

  private val CACHE_KEY = "USER"

  def getUsers(repositories: List[String], daysDataToRetrieve: Int): List[User] = {

    cache.get(CACHE_KEY)
      .getOrElse {
        val pullRequests = repositories.flatMap(repoFullName => {
          val repo = github.getRepository(repoFullName)
          pullRequestRepository.getPullRequests(repo, daysDataToRetrieve)
        })

        val users = buildUsers(pullRequests)

        cache.set(CACHE_KEY, users, 30.minutes)
        users
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
