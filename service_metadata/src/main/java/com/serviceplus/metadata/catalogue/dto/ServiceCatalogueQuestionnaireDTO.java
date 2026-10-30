package com.serviceplus.metadata.catalogue.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class ServiceCatalogueQuestionnaireDTO {

    private Integer masterId;

    private String name;

    private String description;

    private JsonNode data;

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
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

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
