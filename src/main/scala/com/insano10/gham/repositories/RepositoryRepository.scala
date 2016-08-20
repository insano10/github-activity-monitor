package com.insano10.gham.repositories

import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import com.insano10.gham.github.entities.RepositorySummary
import org.kohsuke.github.GitHub

import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._

class RepositoryRepository(github: GitHub, pullRequestRepository: PullRequestRepository) {

  implicit val cache = ScalaCache(GuavaCache())

  def getRepositorySummaries(repositories: List[String], monthsDataToRetrieve: Long): List[RepositorySummary] =

    memoizeSync(30 minutes) {

      val pullRequests = pullRequestRepository.getPullRequests(repositories, monthsDataToRetrieve)

      pullRequests.
        groupBy(pr => pr.repositoryFullName).
        mapValues(prs => {
          val repo = github.getRepository(prs.head.repositoryFullName)
          new RepositorySummary(prs.head.repositoryName, prs, toLocalDateTime(repo.getPushedAt))
        }).
        values.
        toList.
        sortBy(repo => repo.name)
    }

  private def toLocalDateTime(date: Date): LocalDateTime = {
    LocalDateTime.ofInstant(date.toInstant, ZoneId.of("UTC"))
  }
}
