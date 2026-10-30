package com.serviceplus.metadata.dto;

public class UserRoleRequest {

    private Long userId;
    private Integer roleId;

    public UserRoleRequest() {
    }

    public UserRoleRequest(Long userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
