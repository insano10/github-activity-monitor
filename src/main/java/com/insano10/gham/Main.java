package com.insano10.gham;

import com.insano10.gham.entities.PullRequest;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        final Config config = ConfigFactory.load();

        final GitHub github = GitHub.connectUsingOAuth(config.getString("github.oauthToken"));

        final PullRequestFetcher pullRequestFetcher = new PullRequestFetcher(github);

        for (String repo : config.getStringList("repos"))
        {
            final PagedIterable<GHPullRequest> pullRequests = pullRequestFetcher.fetch(repo);

            final PullRequestTransformer pullRequestTransformer = new PullRequestTransformer();
            final List<PullRequest> transformedRequests = pullRequestTransformer.transform(pullRequests, 3);

            final PullRequestAnalyser pullRequestAnalyser = new PullRequestAnalyser();
            final Map<String, PullRequestSummary> summaries = pullRequestAnalyser.analyse(transformedRequests);

            System.out.println(repo);
            for (PullRequestSummary summary : summaries.values())
            {
                System.out.println(summary);
            }
            System.out.println();

        }
    }
}
