package com.serviceplus.metadata.dto;

import java.util.Date;

public class ExternalSystemUserDTO {

    private Long id;

    private Long externalSystemRegId;

    private String clientId;

    private Integer userId;

    private String signNo;

    private Date createdOn;

    public ExternalSystemUserDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExternalSystemRegId() {
        return externalSystemRegId;
    }

    public void setExternalSystemRegId(Long externalSystemRegId) {
        this.externalSystemRegId = externalSystemRegId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSignNo() {
        return signNo;
    }

    public void setSignNo(String signNo) {
        this.signNo = signNo;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
