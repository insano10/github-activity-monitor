package com.insano10.gham.repositories

import java.text.SimpleDateFormat
import java.util.Date

import com.insano10.gham.github.entities.{Commit, RepositorySummary}
import org.kohsuke.github.{GHRepository, GitHub}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._

class RepositoryRepository(github: GitHub, pullRequestRepository: PullRequestRepository) {

  implicit val cache = ScalaCache(GuavaCache())

  private val dateTimeFormat = new SimpleDateFormat("E dd MMM yyyy")

  def getRepositorySummaries(repositories: List[String], monthsDataToRetrieve: Long): List[RepositorySummary] =

    memoizeSync(30 minutes) {

      val pullRequests = pullRequestRepository.getPullRequests(repositories, monthsDataToRetrieve)

      pullRequests.
        groupBy(pr => pr.repositoryFullName).
        mapValues(prs => {
          val repo = github.getRepository(prs.head.repositoryFullName)

          new RepositorySummary(prs.head.repositoryName, prs, toFormattedDateTime(repo.getPushedAt), getMostRecentUserCommit(repo))
        }).
        values.
        toList.
        sortBy(repo => repo.name)
    }

  private def toFormattedDateTime(date: Date): String = {
    dateTimeFormat.format(date)
  }

  private def getMostRecentUserCommit(repo: GHRepository) = {

    val lastGHCommit = repo.queryCommits().pageSize(3).list().iterator().asScala.
      dropWhile(_.getAuthor == null).
      next

    new Commit(lastGHCommit.getAuthor.getLogin, lastGHCommit.getAuthor.getAvatarUrl, lastGHCommit.getHtmlUrl.toString, lastGHCommit.getCommitShortInfo.getMessage)
  }
}
