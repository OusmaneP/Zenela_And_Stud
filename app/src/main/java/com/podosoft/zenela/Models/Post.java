package com.podosoft.zenela.Models;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {

    private Long id;

    private Long posterId;

    private String comment;

    private String type;

    private String fileName;

    private Date createdAt;

    private String posterName;

    private String posterProfile;

    private LikingPossibility likingPossibility;

    private int savingPossibility;

    private List<Comment> commentsList = new ArrayList<>();

    private Long totalComments;

    private boolean notified;

    private String thumbnail;

    public Post() {
    }


    public Post(Long posterId, String comment, String type, String fileName, Date createdAt) {
        this.posterId = posterId;
        this.comment = comment;
        this.type = type;
        this.fileName = fileName;
        this.createdAt = createdAt;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPosterId() {
        return posterId;
    }

    public void setPosterId(Long posterId) {
        this.posterId = posterId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        if (type.contains("image"))
            return "image";
        else if (type.contains("video"))
            return "video";
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreatedAt() {

//        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        return formatter.format(createdAt);
//        return getDay(createdAt)+ " " + getMonth(createdAt) + " " + getYear(createdAt);
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterProfile() {
        return posterProfile;
    }

    public void setPosterProfile(String posterProfile) {
        this.posterProfile = posterProfile;
    }

    public LikingPossibility getLikingPossibility() {
        return likingPossibility;
    }

    public void setLikingPossibility(LikingPossibility likingPossibility) {
        this.likingPossibility = likingPossibility;
    }

    public List<Comment> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }

    public int getSavingPossibility() {
        return savingPossibility;
    }

    public void setSavingPossibility(int savingPossibility) {
        this.savingPossibility = savingPossibility;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    private String getMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);

        switch (month){
            case 0:
                return "Jan";
            case 1:
                return "Fev";
            case 2:
                return "Mars";
            case 3:
                return "Avr";
            case 4:
                return "Mai";
            case 5:
                return "Juin";
            case 6:
                return "Juil";
            case 7:
                return "Aout";
            case 8:
                return "Sept";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return " ";
        }
    }

    private int getYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    private int getDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
