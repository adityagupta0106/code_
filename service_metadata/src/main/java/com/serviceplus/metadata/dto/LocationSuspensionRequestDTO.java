package com.serviceplus.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocationSuspensionRequestDTO {

    @NotNull(message = "serviceId is required")
    private Integer serviceId;

    @NotNull(message = "sduLocationId is required")
    private Integer sduLocationId;

    @NotNull(message = "submissionLocationId is required")
    private Integer submissionLocationId;

    @NotBlank(message = "reason is required")
    private String reason;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getSduLocationId() {
        return sduLocationId;
    }

    public void setSduLocationId(Integer sduLocationId) {
        this.sduLocationId = sduLocationId;
    }

    public Integer getSubmissionLocationId() {
        return submissionLocationId;
    }

    public void setSubmissionLocationId(Integer submissionLocationId) {
        this.submissionLocationId = submissionLocationId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
