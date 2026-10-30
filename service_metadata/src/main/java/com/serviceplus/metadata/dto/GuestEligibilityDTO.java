package com.serviceplus.metadata.dto;

public class GuestEligibilityDTO {

    private Integer serviceId;
    private Integer baseServiceId;
    private String tenantId;
    private Integer stateId;
    private String taskId;
    private String formId;
    private Boolean registrationRequired;

    public GuestEligibilityDTO() {
    }

    public GuestEligibilityDTO(
            Integer serviceId,
            Integer baseServiceId,
            String tenantId,
            Integer stateId,
            String taskId,
            String formId,
            Boolean registrationRequired) {

        this.serviceId = serviceId;
        this.baseServiceId = baseServiceId;
        this.tenantId = tenantId;
        this.stateId = stateId;
        this.taskId = taskId;
        this.formId = formId;
        this.registrationRequired = registrationRequired;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }
    
    public Boolean getRegistrationRequired() {
        return registrationRequired;
    }

    public void setRegistrationRequired(Boolean registrationRequired) {
        this.registrationRequired = registrationRequired;
    }
}