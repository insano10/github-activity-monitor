package com.insano10.gham.entities;

import java.time.LocalDateTime;

public class Comment
{
    private String owner;
    private LocalDateTime created;

    public Comment(String owner, LocalDateTime created)
    {
        this.owner = owner;
        this.created = created;
    }

    public String getOwner()
    {
        return owner;
    }

    public LocalDateTime getCreated()
    {
        return created;
    }

    @Override
    public String toString()
    {
        return "Comment{" +
                "owner='" + owner + '\'' +
                ", created=" + created +
                '}';
    }
}
