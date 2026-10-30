package com.serviceplus.metadata.aadhaarConfiguration.dto;

public class SaveAuaResponseMappingRequest {

    private String nodeCode;
    private String responseAttributeType;
    private String desiredResponse;

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getResponseAttributeType() {
        return responseAttributeType;
    }

    public void setResponseAttributeType(String responseAttributeType) {
        this.responseAttributeType = responseAttributeType;
    }

    public String getDesiredResponse() {
        return desiredResponse;
    }

    public void setDesiredResponse(String desiredResponse) {
        this.desiredResponse = desiredResponse;
    }
}
