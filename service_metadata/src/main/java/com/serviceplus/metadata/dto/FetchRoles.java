package com.serviceplus.metadata.dto;

public class FetchRoles{

    private String userId;

    public String getUserName() {
        return userId;
    }

    public void setUserName(String userId) {
        this.userId = userId;
    }

    public FetchRoles(String userId) {
        super();
        this.userId = userId;
    }

    public FetchRoles() {
    }
}
