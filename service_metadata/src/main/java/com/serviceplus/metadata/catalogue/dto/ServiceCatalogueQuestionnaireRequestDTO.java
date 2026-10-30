package com.serviceplus.metadata.catalogue.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class ServiceCatalogueQuestionnaireRequestDTO {

    private Long configurationId;

    private Integer masterId;

    private Boolean enabled;

    private JsonNode answers;

    public Long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public JsonNode getAnswers() {
        return answers;
    }

    public void setAnswers(JsonNode answers) {
        this.answers = answers;
    }
}