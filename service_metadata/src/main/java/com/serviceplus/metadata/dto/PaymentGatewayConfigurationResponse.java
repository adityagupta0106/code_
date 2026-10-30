package com.serviceplus.metadata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

public class PaymentGatewayConfigurationResponse {

    private Integer serviceId;
    private String gatewayCode;
    private JsonNode gatewayConfiguration;
    private JsonNode mappings;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode historyMappings;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode oldMappings;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean historyEnabled;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean oldEnabled;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getGatewayCode() {
        return gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    public JsonNode getGatewayConfiguration() {
        return gatewayConfiguration;
    }

    public void setGatewayConfiguration(JsonNode gatewayConfiguration) {
        this.gatewayConfiguration = gatewayConfiguration;
    }

    public JsonNode getMappings() {
        return mappings;
    }

    public void setMappings(JsonNode mappings) {
        this.mappings = mappings;
    }

    public JsonNode getHistoryMappings() {
        return historyMappings;
    }

    public void setHistoryMappings(JsonNode historyMappings) {
        this.historyMappings = historyMappings;
        if (this.oldMappings == null) {
            this.oldMappings = historyMappings;
        }
    }

    public JsonNode getOldMappings() {
        return oldMappings;
    }

    public void setOldMappings(JsonNode oldMappings) {
        this.oldMappings = oldMappings;
        if (this.historyMappings == null) {
            this.historyMappings = oldMappings;
        }
    }

    public Boolean getHistoryEnabled() {
        return historyEnabled;
    }

    public void setHistoryEnabled(Boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
        if (this.oldEnabled == null) {
            this.oldEnabled = historyEnabled;
        }
    }

    public Boolean getOldEnabled() {
        return oldEnabled;
    }

    public void setOldEnabled(Boolean oldEnabled) {
        this.oldEnabled = oldEnabled;
        if (this.historyEnabled == null) {
            this.historyEnabled = oldEnabled;
        }
    }
}
