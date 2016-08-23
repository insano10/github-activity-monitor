package com.insano10.gham.repositories

import java.io.IOException
import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import com.insano10.gham.entities.GithubEntities.{Comment, PullRequest}
import org.kohsuke.github._

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scalacache.ScalaCache
import scalacache.guava.GuavaCache
import scalacache.memoization._

class PullRequestRepository(github: GitHub) {

  implicit val cache = ScalaCache(GuavaCache())

  def getPullRequests(repositories: List[String], monthsDataToRetrieve: Long): List[PullRequest] = {

    memoizeSync(30 minutes) {

      repositories.flatMap(repo => {
        val pullRequests = fetchPullRequests(repo)
        transform(pullRequests, monthsDataToRetrieve)
      })
    }
  }

  def fetchPullRequests(repository: String): PagedIterable[GHPullRequest] = {

    try {
      val repo = github.getRepository(repository)
      repo.queryPullRequests.base("master").direction(GHDirection.DESC).state(GHIssueState.ALL).list.withPageSize(10)
    }
    catch {
      case e: IOException => throw new RuntimeException("Failed to get pull requests from repository " + repository, e)

    }
  }

  def transform(pullRequests: PagedIterable[GHPullRequest], monthsRetrieved: Long): List[PullRequest] = {

    val minDate: LocalDateTime = LocalDateTime.now.minusMonths(monthsRetrieved)

    try {

      pullRequests.iterator().asScala.
        takeWhile(pr => toLocalDateTime(pr.getCreatedAt).isAfter(minDate)).
        map(pr => {
          val pullRequest: PullRequest = new PullRequest(pr.getRepository.getName,
            pr.getRepository.getFullName,
            pr.getUser.getLogin,
            pr.getTitle,
            toLocalDateTime(pr.getCreatedAt),
            toOptionalLocalDateTime(pr.getClosedAt))

          for (comment <- pr.getComments.asScala) {
            pullRequest.addComment(new Comment(comment.getUser.getLogin, toLocalDateTime(comment.getUpdatedAt)))
          }
          pullRequest
        }).
        toList
    }
    catch {
      case e: IOException => throw new RuntimeException("Failed to transform pull request", e)
    }
  }

  private def toLocalDateTime(date: Date): LocalDateTime = {
    LocalDateTime.ofInstant(date.toInstant, ZoneId.of("UTC"))
  }

  private def toOptionalLocalDateTime(date: Date): Option[LocalDateTime] = date match {
    case null => None
    case d => Some(toLocalDateTime(d))
  }
}
