package com.serviceplus.metadata.aadhaarConfiguration.dto;

import java.util.List;

public class SaveAuaApiMappingRequest {

    private String apiType;
    private List<SaveAuaFieldMappingRequest> requestMappings;
    private List<SaveAuaResponseMappingRequest> responseMappings;

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public List<SaveAuaFieldMappingRequest> getRequestMappings() {
        return requestMappings;
    }

    public void setRequestMappings(List<SaveAuaFieldMappingRequest> requestMappings) {
        this.requestMappings = requestMappings;
    }

    public List<SaveAuaResponseMappingRequest> getResponseMappings() {
        return responseMappings;
    }

    public void setResponseMappings(List<SaveAuaResponseMappingRequest> responseMappings) {
        this.responseMappings = responseMappings;
    }
}