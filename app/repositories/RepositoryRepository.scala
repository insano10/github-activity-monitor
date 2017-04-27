package repositories

import gocd.{DeploymentStatusRetriever, NoOpDeploymentStatusRetriever}
import models.GithubEntities.{Commit, RepositorySummary}
import org.kohsuke.github.{GHRepository, GitHub}
import play.api.cache.CacheApi

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class RepositoryRepository(github: GitHub, pullRequestRepository: PullRequestRepository, cache: CacheApi) {

  private val CACHE_KEY = "REPO"

  private var deploymentStatusRetriever: DeploymentStatusRetriever = new NoOpDeploymentStatusRetriever()

  def setDeploymentStatusRetriever(deploymentStatusRetriever: DeploymentStatusRetriever): Unit = {
    this.deploymentStatusRetriever = deploymentStatusRetriever
  }

  def getRepositorySummaries(repositories: List[String], daysDataToRetrieve: Int)(implicit ec: ExecutionContext): Future[List[RepositorySummary]] =

    Future.successful(
      cache.get(CACHE_KEY)
        .getOrElse {
          val repositorySummaries = repositories.map(repoFullName => {

            val repo = github.getRepository(repoFullName)
            val pullRequests = pullRequestRepository.getPullRequests(repo, daysDataToRetrieve)

            val openPullRequests = pullRequests.count(e => e.closedTimeMs.isEmpty)

            new RepositorySummary(
              repo.getName,
              repo.getHtmlUrl.toString,
              pullRequests,
              getMostRecentUserCommit(repo),
              openPullRequests,
              deploymentStatusRetriever.getDeploymentStatus(repo),
              deploymentStatusRetriever.deploymentUrl(repoFullName))
          }).sortBy(repo => repo.name)

          cache.set(CACHE_KEY, repositorySummaries, 10.minutes)
          repositorySummaries
        })

  private def getMostRecentUserCommit(repo: GHRepository) = {

    val lastGHCommit = repo.queryCommits().pageSize(3).list().iterator().asScala.
      dropWhile(_.getAuthor == null).
      next

    new Commit(
      lastGHCommit.getAuthor.getLogin,
      lastGHCommit.getAuthor.getAvatarUrl,
      lastGHCommit.getHtmlUrl.toString,
      lastGHCommit.getCommitShortInfo.getMessage,
      lastGHCommit.getCommitShortInfo.getCommitter.getDate.getTime)
  }
}
