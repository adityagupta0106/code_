package com.serviceplus.metadata.aadhaarConfiguration.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class AadhaarConfigurationRequest {

    private String type;

    private JsonNode configuration;

    private String licenseKey;

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public JsonNode getConfiguration() {
        return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
        this.configuration = configuration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
