package com.insano10.gham;

public class PullRequestSummary {

    private String user;
    private int totalPullRequestsRaised;
    private int totalPullRequestsCommentedOn;
    private long avgMinsToClose;
    private long avgMinsToFirstComment;

    public PullRequestSummary(String user)
    {
        this.user = user;
        this.totalPullRequestsRaised = 0;
        this.totalPullRequestsCommentedOn = 0;
        this.avgMinsToClose = 0;
        this.avgMinsToFirstComment = 0;
    }

    public void pullRequestRaised(long minsOpen) {
        this.avgMinsToClose = ((avgMinsToClose * totalPullRequestsRaised) + minsOpen) / (totalPullRequestsRaised + 1);
        this.totalPullRequestsRaised++;
    }

    public void pullRequestCommentedOn(long minsTillFirstComment)
    {
        this.avgMinsToFirstComment = ((avgMinsToFirstComment * totalPullRequestsCommentedOn) + minsTillFirstComment) / (totalPullRequestsCommentedOn + 1);
        this.totalPullRequestsCommentedOn++;
    }

    public String getUser()
    {
        return user;
    }

    public int getTotalPullRequestsRaised()
    {
        return totalPullRequestsRaised;
    }

    public int getTotalPullRequestsCommentedOn()
    {
        return totalPullRequestsCommentedOn;
    }

    public long getAvgMinsToClose()
    {
        return avgMinsToClose;
    }

    public long getAvgMinsToFirstComment()
    {
        return avgMinsToFirstComment;
    }

    @Override
    public String toString()
    {
        return "PullRequestSummary{" +
                "user='" + user + '\'' +
                ", totalPullRequestsRaised=" + totalPullRequestsRaised +
                ", totalPullRequestsCommentedOn=" + totalPullRequestsCommentedOn +
                ", avgMinsToClose=" + avgMinsToClose  +
                ", avgMinsToFirstComment=" + avgMinsToFirstComment +
                '}';
    }
}
