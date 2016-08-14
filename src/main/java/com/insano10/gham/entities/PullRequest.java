package com.insano10.gham.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PullRequest
{
    private String owner;
    private String title;
    private LocalDateTime created;
    private LocalDateTime closed;
    private List<Comment> comments;

    public PullRequest(String owner, String title, LocalDateTime created, LocalDateTime closed)
    {
        this.owner = owner;
        this.title = title;
        this.created = created;
        this.closed = closed;
        this.comments = new ArrayList<>();
    }

    public void addComment(Comment comment)
    {
        comments.add(comment);
    }

    public String getOwner()
    {
        return owner;
    }

    public String getTitle()
    {
        return title;
    }

    public LocalDateTime getCreated()
    {
        return created;
    }

    public LocalDateTime getClosed()
    {
        return closed;
    }

    public List<Comment> getComments()
    {
        return comments;
    }

    @Override
    public String toString()
    {
        return "PullRequest{" +
                "owner='" + owner + '\'' +
                ", title='" + title + '\'' +
                ", created=" + created +
                ", closed=" + closed +
                ", comments=" + comments +
                '}';
    }
}
