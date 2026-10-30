package com.serviceplus.metadata.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class SubmissionValidationRequestDTO {

    @NotNull(message = "serviceId is required")
    private Integer serviceId;

    private Integer submissionLocationId;

    private List<Integer> submissionLocationIds;

    private Integer sduLocationId;

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

    public List<Integer> getSubmissionLocationIds() {
        return submissionLocationIds;
    }

    public void setSubmissionLocationIds(List<Integer> submissionLocationIds) {
        this.submissionLocationIds = submissionLocationIds;
    }

    public Integer getSduLocationId() {
        return sduLocationId;
    }

    public void setSduLocationId(Integer sduLocationId) {
        this.sduLocationId = sduLocationId;
    }
}
