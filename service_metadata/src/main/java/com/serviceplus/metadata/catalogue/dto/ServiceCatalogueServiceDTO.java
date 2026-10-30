package com.serviceplus.metadata.catalogue.dto;

public class ServiceCatalogueServiceDTO {

    private Long serviceId;

    private String serviceCode;

    private String serviceName;

    private String status;

    private boolean locked;

    private Long lockedConfigurationId;

    private String lockedConfigurationCode;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Long getLockedConfigurationId() {
        return lockedConfigurationId;
    }

    public void setLockedConfigurationId(
            Long lockedConfigurationId) {

        this.lockedConfigurationId =
                lockedConfigurationId;
    }

    public String getLockedConfigurationCode() {
        return lockedConfigurationCode;
    }

    public void setLockedConfigurationCode(
            String lockedConfigurationCode) {

        this.lockedConfigurationCode =
                lockedConfigurationCode;
    }
}
