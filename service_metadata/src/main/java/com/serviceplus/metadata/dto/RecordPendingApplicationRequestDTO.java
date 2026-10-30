package com.serviceplus.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RecordPendingApplicationRequestDTO {

    @NotBlank(message = "applicationId is required")
    private String applicationId;

    @NotNull(message = "serviceId is required")
    private Integer serviceId;

    @NotNull(message = "submissionLocationId is required")
    private Integer submissionLocationId;

    private Integer sduLocationId;

    private String slaStartPolicy;

    private Long citizenUserId;

    private String acknowledgementNumber;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getSubmissionLocationId() {
        return submissionLocationId;
    }

    public void setSubmissionLocationId(Integer submissionLocationId) {
        this.submissionLocationId = submissionLocationId;
    }

    public Integer getSduLocationId() {
        return sduLocationId;
    }

    public void setSduLocationId(Integer sduLocationId) {
        this.sduLocationId = sduLocationId;
    }

    public String getSlaStartPolicy() {
        return slaStartPolicy;
    }

    public void setSlaStartPolicy(String slaStartPolicy) {
        this.slaStartPolicy = slaStartPolicy;
    }

    public Long getCitizenUserId() {
        return citizenUserId;
    }

    public void setCitizenUserId(Long citizenUserId) {
        this.citizenUserId = citizenUserId;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public void setAcknowledgementNumber(String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }
}
