package com.serviceplus.metadata.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class AdoptSubmissionLocationsRequestDTO {

    @NotNull(message = "serviceId is required")
    private Integer serviceId;

    @NotNull(message = "sduLevelId is required")
    private Integer sduLevelId;

    @NotNull(message = "sduLocationId is required")
    private Integer sduLocationId;

    private Integer submissionLevelId;

    @NotEmpty(message = "submissionLocationIds cannot be empty")
    private List<Integer> submissionLocationIds;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getSduLevelId() {
        return sduLevelId;
    }

    public void setSduLevelId(Integer sduLevelId) {
        this.sduLevelId = sduLevelId;
    }

    public Integer getSduLocationId() {
        return sduLocationId;
    }

    public void setSduLocationId(Integer sduLocationId) {
        this.sduLocationId = sduLocationId;
    }

    public Integer getSubmissionLevelId() {
        return submissionLevelId;
    }

    public void setSubmissionLevelId(Integer submissionLevelId) {
        this.submissionLevelId = submissionLevelId;
    }

    public List<Integer> getSubmissionLocationIds() {
        return submissionLocationIds;
    }

    public void setSubmissionLocationIds(List<Integer> submissionLocationIds) {
        this.submissionLocationIds = submissionLocationIds;
    }
}
