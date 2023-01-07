package com.podosoft.zenela.Responses;

import com.podosoft.zenela.Models.Comment;
import com.podosoft.zenela.Models.User;

import java.util.List;

public class CommentResponse {
    private String body;
    private String status;
    private User principal;
    private List<Comment> commentList;

    public CommentResponse() {
    }

    public CommentResponse(String body, String status) {
        this.body = body;
        this.status = status;
    }

    public CommentResponse(String body, String status, User principal) {
        this.body = body;
        this.status = status;
        this.principal = principal;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getPrincipal() {
        return principal;
    }

    public void setPrincipal(User principal) {
        this.principal = principal;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}
