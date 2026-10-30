package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;
import java.util.Date;

public class BsaConfigurationStatusResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long departmentId;
    private String tenantId;
    private String registrationStatus;
    private boolean hasConfiguration;
    private String serviceCode;
    private String message;
    private Date lastUpdated;

    public BsaConfigurationStatusResponseDTO() {
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public boolean isHasConfiguration() {
        return hasConfiguration;
    }

    public void setHasConfiguration(boolean hasConfiguration) {
        this.hasConfiguration = hasConfiguration;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
