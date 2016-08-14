package com.insano10.gham;

import com.insano10.gham.entities.Comment;
import com.insano10.gham.entities.PullRequest;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.PagedIterable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PullRequestTransformer
{
    public List<PullRequest> transform(PagedIterable<GHPullRequest> pullRequests, int monthsToRetrieve)
    {
        List<PullRequest> transformedRequests = new ArrayList<>();

        final LocalDateTime minDate = LocalDateTime.now().minusMonths(monthsToRetrieve);

        try
        {
            for (GHPullRequest pr : pullRequests)
            {
                if (pr.getClosedAt() != null)
                {
                    final PullRequest pullRequest = new PullRequest(pr.getUser().getLogin(),
                                                                    pr.getTitle(),
                                                                    toLocalDateTime(pr.getCreatedAt()),
                                                                    toLocalDateTime(pr.getClosedAt()));

                    for (GHIssueComment comment : pr.getComments())
                    {
                        pullRequest.addComment(new Comment(comment.getUser().getLogin(),
                                                           toLocalDateTime(comment.getUpdatedAt())));
                    }

                    transformedRequests.add(pullRequest);

                    if (pullRequest.getCreated().isBefore(minDate))
                    {
                        break;
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to transform pull request", e);
        }

        return transformedRequests;
    }

    private LocalDateTime toLocalDateTime(Date date)
    {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
    }
}

