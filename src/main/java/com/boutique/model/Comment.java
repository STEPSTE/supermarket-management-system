package com.boutique.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Embeddable
public class Comment {
    
    @ManyToOne
    @JoinColumn(name = "comment_user_id")
    private User user;
    
    @Column(name = "comment_content", length = 1000)
    private String content;
    
    @Column(name = "comment_rating")
    private Integer rating;
    
    @Column(name = "comment_created_at")
    private LocalDateTime createdAt;

    public Comment() {
        this.createdAt = LocalDateTime.now();
    }

    public Comment(User user, String content, Integer rating) {
        this();
        this.user = user;
        this.content = content;
        this.rating = rating;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}