package com.podosoft.zenela.Dto;

public class CommentDto {

    private Long postId;

    private String commentText;

    public CommentDto() {
    }

    public CommentDto(Long postId, String commentText) {
        this.postId = postId;
        this.commentText = commentText;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
