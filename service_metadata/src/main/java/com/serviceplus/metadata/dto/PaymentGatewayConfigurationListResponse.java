package com.serviceplus.metadata.dto;

public class PaymentGatewayConfigurationListResponse {

    private Long id;
    private String gatewayCode;
    private String gatewayName;
    private String scope;
    private Boolean enabled;

    public PaymentGatewayConfigurationListResponse() {
    }

    public PaymentGatewayConfigurationListResponse(
            Long id,
            String gatewayCode,
            String gatewayName,
            String scope,
            Boolean enabled) {

        this.id = id;
        this.gatewayCode = gatewayCode;
        this.gatewayName = gatewayName;
        this.scope = scope;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGatewayCode() {
        return gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}