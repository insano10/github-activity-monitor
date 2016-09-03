package com.insano10.gham.github.repositories

import java.text.SimpleDateFormat

import com.insano10.gham.entities.GithubEntities.{Commit, RepositorySummary}
import com.insano10.gham.github.{DeploymentStatusRetriever, NoOpDeploymentStatusRetriever}
import org.kohsuke.github.{GHRepository, GitHub}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._

class RepositoryRepository(github: GitHub, pullRequestRepository: PullRequestRepository) {

  implicit val cache = ScalaCache(GuavaCache())

  private val dateTimeFormat = new SimpleDateFormat("E dd MMM yyyy")
  private var deploymentStatusRetriever: DeploymentStatusRetriever = new NoOpDeploymentStatusRetriever()

  def setDeploymentStatusRetriever(deploymentStatusRetriever: DeploymentStatusRetriever): Unit = {
    this.deploymentStatusRetriever = deploymentStatusRetriever
  }

  def getRepositorySummaries(repositories: List[String], daysDataToRetrieve: Int)(implicit ec: ExecutionContext): Future[List[RepositorySummary]] =

    memoizeSync(30 minutes) {

      val pullRequests = pullRequestRepository.getPullRequests(repositories, daysDataToRetrieve)

      val repositorySummaries = pullRequests.
        groupBy(pr => pr.repositoryFullName).
        mapValues(prs => {
          val repo = github.getRepository(prs.head.repositoryFullName)
          val openPullRequests = prs.count(e => e.closedTimeMs.isEmpty)

          new RepositorySummary(prs.head.repositoryName, prs, repo.getPushedAt.getTime, getMostRecentUserCommit(repo),
            openPullRequests, deploymentStatusRetriever.repositoryNeedsDeployment(repo.getFullName))
        }).
        values.
        toList.
        sortBy(repo => repo.name)

      Future.successful(repositorySummaries)
    }

  private def getMostRecentUserCommit(repo: GHRepository) = {

    val lastGHCommit = repo.queryCommits().pageSize(3).list().iterator().asScala.
      dropWhile(_.getAuthor == null).
      next

    new Commit(lastGHCommit.getAuthor.getLogin, lastGHCommit.getAuthor.getAvatarUrl, lastGHCommit.getHtmlUrl.toString, lastGHCommit.getCommitShortInfo.getMessage)
  }
}
