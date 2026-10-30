package com.serviceplus.metadata.aadhaarConfiguration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuaApiMappingResponse {

    private Long apiId;

    private String apiCode;

    private String apiName;

    private String operationType;

    private String apiVersion;

    private List<AuaApiMessageMappingResponse> messages;

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public List<AuaApiMessageMappingResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<AuaApiMessageMappingResponse> messages) {
        this.messages = messages;
    }
}
