package com.podosoft.zenela.Models;

import java.io.Serializable;

public class UsersRoles implements Serializable {

    private Long id;

    private Long userId;

    private Long roleId;

    public UsersRoles(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UsersRoles() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
