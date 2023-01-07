package com.podosoft.zenela.Responses;

import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;

import java.io.Serializable;
import java.util.ArrayList;

public class RandomPostsResponse implements Serializable {
    private String body;
    private String status;
    private ArrayList<Post> posts = new ArrayList<>();
    private User principal;

    public RandomPostsResponse(String body, String status) {
        this.body = body;
        this.status = status;
    }

    public RandomPostsResponse(String body, String status, ArrayList<Post> posts, User principal) {
        this.body = body;
        this.status = status;
        this.posts = posts;
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

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public User getPrincipal() {
        return principal;
    }

    public void setPrincipal(User principal) {
        this.principal = principal;
    }
}
