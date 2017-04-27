package repositories

import java.io.IOException
import java.util.Date

import models.GithubEntities.PullRequest
import org.kohsuke.github._
import play.api.cache.CacheApi

import scala.concurrent.duration._
import scala.collection.JavaConverters._

class PullRequestRepository(val cache: CacheApi) {

  private val CACHE_KEY = "PR"

  def getPullRequests(repository: GHRepository, daysDataToRetrieve: Int): List[PullRequest] = {

    cache.get(CACHE_KEY)
      .getOrElse {
        val pullRequests = fetchPullRequests(repository)
        val formattedPullRequests = transform(pullRequests, daysDataToRetrieve)
        cache.set(CACHE_KEY, formattedPullRequests, 10.minutes)

        formattedPullRequests
      }
  }

  private def fetchPullRequests(repository: GHRepository): PagedIterable[GHPullRequest] = {

    try {
      repository.queryPullRequests.base("master").direction(GHDirection.DESC).state(GHIssueState.ALL).list.withPageSize(10)
    }
    catch {
      case e: IOException => throw new RuntimeException("Failed to get pull requests from repository " + repository.getFullName, e)

    }
  }

  private def transform(pullRequests: PagedIterable[GHPullRequest], daysRetrieved: Int): List[PullRequest] = {

    val minTime = System.currentTimeMillis() - (daysRetrieved * 24 * 60 * 60 * 1000L)

    try {

      pullRequests.iterator().asScala.
        takeWhile(pr => pr.getCreatedAt.getTime > minTime).
        map(pr => {
          new PullRequest(pr.getRepository.getName,
            pr.getRepository.getFullName,
            pr.getUser.getLogin,
            pr.getTitle,
            pr.getCreatedAt.getTime,
            dateToOptMillis(pr.getClosedAt))
        }).
        toList
    }
    catch {
      case e: IOException => throw new RuntimeException("Failed to transform pull request", e)
    }
  }

  private def dateToOptMillis(date: Date) = date match {
    case null => None
    case x => Some(x.getTime)
  }

}
