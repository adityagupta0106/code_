package com.serviceplus.metadata.esign.dto;

import jakarta.validation.constraints.NotNull;

public class ServiceEsignToggleDTO {

    @NotNull(message = "Service ID is mandatory")
    private Integer serviceId;

    @NotNull(message = "eSign enabled flag is mandatory")
    private Boolean esignEnabled;

    public ServiceEsignToggleDTO() {
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Boolean getEsignEnabled() {
        return esignEnabled;
    }

    public void setEsignEnabled(Boolean esignEnabled) {
        this.esignEnabled = esignEnabled;
    }
}
