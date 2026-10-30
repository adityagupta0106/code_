package com.serviceplus.metadata.aadhaarConfiguration.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class AadhaarConfigurationResponse {

    private Long id;
    private Integer serviceId;
    private String type;
    private JsonNode configuration;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public JsonNode getConfiguration() {
        return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
        this.configuration = configuration;
    }
}
