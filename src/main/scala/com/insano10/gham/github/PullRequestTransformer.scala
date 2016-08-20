package com.insano10.gham.github

import java.io.IOException
import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import com.insano10.gham.github.entities.{Comment, PullRequest}
import org.kohsuke.github.{GHPullRequest, PagedIterable}
import scala.collection.JavaConverters._

class PullRequestTransformer {

  def transform(pullRequests: PagedIterable[GHPullRequest], monthsToRetrieve: Long): List[PullRequest] = {

    val minDate: LocalDateTime = LocalDateTime.now.minusMonths(monthsToRetrieve)

    try {

      pullRequests.iterator().asScala.
        takeWhile(pr => toLocalDateTime(pr.getCreatedAt).isAfter(minDate)).
        map(pr => {
          val pullRequest: PullRequest = new PullRequest(pr.getUser.getLogin, pr.getTitle, toLocalDateTime(pr.getCreatedAt), toOptionalLocalDateTime(pr.getClosedAt))

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
