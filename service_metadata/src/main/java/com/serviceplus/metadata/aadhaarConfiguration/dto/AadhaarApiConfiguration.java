package com.serviceplus.metadata.aadhaarConfiguration.dto;

import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarApiType;

import java.io.Serializable;

public class AadhaarApiConfiguration implements Serializable {

    private AadhaarApiType apiType;

    private String displayName;

    private String endpoint;

    private String xmlNamespace;

    private AadhaarPayloadConfiguration requestPayload;

    private AadhaarPayloadConfiguration responsePayload;


    public AadhaarApiConfiguration() {
    }


    public AadhaarApiType getApiType() {
        return apiType;
    }

    public void setApiType(AadhaarApiType apiType) {
        this.apiType = apiType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getXmlNamespace() {
        return xmlNamespace;
    }

    public void setXmlNamespace(String xmlNamespace) {
        this.xmlNamespace = xmlNamespace;
    }

    public AadhaarPayloadConfiguration getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(
            AadhaarPayloadConfiguration requestPayload
    ) {
        this.requestPayload = requestPayload;
    }

    public AadhaarPayloadConfiguration getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(
            AadhaarPayloadConfiguration responsePayload
    ) {
        this.responsePayload = responsePayload;
    }
}
