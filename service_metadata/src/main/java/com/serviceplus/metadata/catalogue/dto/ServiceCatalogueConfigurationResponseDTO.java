package com.serviceplus.metadata.catalogue.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class ServiceCatalogueConfigurationResponseDTO {

    private Long configurationId;

    private String configurationCode;

    private String name;

    private String description;

    private String statusName;

    private List<ServiceDetailDTO> services;

    private JsonNode data;

    public Long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    public String getConfigurationCode() {
        return configurationCode;
    }

    public void setConfigurationCode(String configurationCode) {
        this.configurationCode = configurationCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<ServiceDetailDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceDetailDTO> services) {
        this.services = services;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
