package com.insano10.gham;

import com.insano10.gham.entities.Comment;
import com.insano10.gham.entities.PullRequest;

import java.time.temporal.ChronoUnit;
import java.util.*;

public class PullRequestAnalyser
{

    public Map<String, PullRequestSummary> analyse(List<PullRequest> pullRequests)
    {
        final Map<String, PullRequestSummary> summaries = new HashMap<>();

        for (PullRequest pullRequest : pullRequests)
        {
            final PullRequestSummary summary = getSummary(pullRequest.getOwner(), summaries);

            final long minsOpen = pullRequest.getCreated().until(pullRequest.getClosed(), ChronoUnit.MINUTES);
            summary.pullRequestRaised(minsOpen);

            Set<String> commentersSeen = new HashSet<>();
            for (Comment comment : pullRequest.getComments())
            {
                if (!commentersSeen.contains(comment.getOwner()))
                {
                    final PullRequestSummary commenterSummary = getSummary(comment.getOwner(), summaries);

                    final long minsTillFirstComment = pullRequest.getCreated().until(comment.getCreated(), ChronoUnit.MINUTES);

                    commenterSummary.pullRequestCommentedOn(minsTillFirstComment);
                    commentersSeen.add(comment.getOwner());
                }
            }
        }

        return summaries;
    }

    private PullRequestSummary getSummary(String user, Map<String, PullRequestSummary> summaries)
    {
        return summaries.computeIfAbsent(user, PullRequestSummary::new);
    }
}

