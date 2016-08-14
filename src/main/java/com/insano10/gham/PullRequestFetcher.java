package com.insano10.gham;

import org.kohsuke.github.*;

import java.io.IOException;

public class PullRequestFetcher
{
    private final GitHub github;

    public PullRequestFetcher(GitHub github) {

        this.github = github;
    }

    public PagedIterable<GHPullRequest> fetch(String repository) {

        final GHRepository repo;
        try
        {
            repo = github.getRepository(repository);
            return repo.queryPullRequests().base("master").direction(GHDirection.DESC).state(GHIssueState.ALL).list().withPageSize(10);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to get pull requests from repository " + repository, e);
        }
    }
}
