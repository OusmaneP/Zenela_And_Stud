package com.podosoft.zenela.Responses;

import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Models.User;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchResponse implements Serializable {
    private String body;
    private String status;
    private ArrayList<User> users = new ArrayList<>();

    public SearchResponse() {
    }

    public SearchResponse(String body, String status) {
        this.body = body;
        this.status = status;
    }

    public SearchResponse(String body, String status, ArrayList<User> users) {
        this.body = body;
        this.status = status;
        this.users = users;
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

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
