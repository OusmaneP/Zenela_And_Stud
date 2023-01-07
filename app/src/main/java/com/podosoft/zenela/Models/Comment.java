package com.podosoft.zenela.Models;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {

    private Long id;

    private Long commenter;

    private Long post;

    private String commentText;

    private Date createdAt;

    private String commenterName;

    private String commenterProfile;

    public Comment() {
    }

    public Comment(Long commenter, Long post, String commentText, Date createdAt) {
        this.commenter = commenter;
        this.post = post;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    public Comment(Long commenter, Long post, String commentText, Date createdAt, String commenterName, String commenterProfile) {
        this.commenter = commenter;
        this.post = post;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.commenterName = commenterName;
        this.commenterProfile = commenterProfile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommenter() {
        return commenter;
    }

    public void setCommenter(Long commenter) {
        this.commenter = commenter;
    }

    public Long getPost() {
        return post;
    }

    public void setPost(Long post) {
        this.post = post;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommenterProfile() {
        return commenterProfile;
    }

    public void setCommenterProfile(String commenterProfile) {
        this.commenterProfile = commenterProfile;
    }
}
