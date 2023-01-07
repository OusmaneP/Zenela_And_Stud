package com.podosoft.zenela.Models;

import java.io.Serializable;
import java.util.Date;

public class Like implements Serializable {

    private Long id;

    private Long liker;

    private Long post;

    private Date createdAt;

    public Like() {
    }

    public Like(Long liker, Long post, Date createdAt) {
        this.liker = liker;
        this.post = post;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLiker() {
        return liker;
    }

    public void setLiker(Long liker) {
        this.liker = liker;
    }

    public Long getPost() {
        return post;
    }

    public void setPost(Long post) {
        this.post = post;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
