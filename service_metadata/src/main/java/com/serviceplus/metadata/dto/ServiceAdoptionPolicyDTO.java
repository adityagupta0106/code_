package com.serviceplus.metadata.dto;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceAdoptionPolicyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "serviceId is required")
    private Integer serviceId;

    @NotBlank(message = "nonAdoptedSubmissionPolicy is required")
    private String nonAdoptedSubmissionPolicy; // ALLOW_NON_ADOPTED_SUBMISSION / YES or BLOCK_NON_ADOPTED_SUBMISSION / NO

    private String remarks; // Department Order / Policy Reference Remarks

    public ServiceAdoptionPolicyDTO() {
    }

    public ServiceAdoptionPolicyDTO(Integer serviceId, String nonAdoptedSubmissionPolicy, String remarks) {
        this.serviceId = serviceId;
        this.nonAdoptedSubmissionPolicy = nonAdoptedSubmissionPolicy;
        this.remarks = remarks;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getNonAdoptedSubmissionPolicy() {
        return nonAdoptedSubmissionPolicy;
    }

    public void setNonAdoptedSubmissionPolicy(String nonAdoptedSubmissionPolicy) {
        this.nonAdoptedSubmissionPolicy = nonAdoptedSubmissionPolicy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
