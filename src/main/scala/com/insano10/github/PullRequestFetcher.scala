package com.insano10.github

import java.io.IOException

import org.kohsuke.github._

class PullRequestFetcher(github: GitHub) {

  def fetch(repository: String): PagedIterable[GHPullRequest] = {

    try {
      val repo = github.getRepository(repository)
      repo.queryPullRequests.base("master").direction(GHDirection.DESC).state(GHIssueState.ALL).list.withPageSize(10)
    }
    catch {
      case e: IOException => throw new RuntimeException("Failed to get pull requests from repository " + repository, e)

    }
  }
}
