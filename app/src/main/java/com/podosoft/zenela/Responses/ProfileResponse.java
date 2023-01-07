package com.podosoft.zenela.Responses;

import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ProfileResponse implements Serializable {
    private String body;
    private String status;
    private User principal;
    private ArrayList<User> myFriends = new ArrayList<>();
    private ArrayList<User> friendRequests = new ArrayList<>();
    private ArrayList<User> invitedFriends = new ArrayList<>();
    private ArrayList<Post> posts = new ArrayList<>();
    private Boolean friends;

    public ProfileResponse() {
    }

    public ProfileResponse(String body, String status) {
        this.body = body;
        this.status = status;
    }

    public ProfileResponse(String body, String status, User principal) {
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

    public ArrayList<User> getMyFriends() {
        return myFriends;
    }

    public void setMyFriends(ArrayList<User> myFriends) {
        this.myFriends = myFriends;
    }

    public ArrayList<User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(ArrayList<User> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public ArrayList<User> getInvitedFriends() {
        return invitedFriends;
    }

    public void setInvitedFriends(ArrayList<User> invitedFriends) {
        this.invitedFriends = invitedFriends;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public Boolean getFriends() {
        return friends;
    }

    public void setFriends(Boolean friends) {
        this.friends = friends;
    }
}
