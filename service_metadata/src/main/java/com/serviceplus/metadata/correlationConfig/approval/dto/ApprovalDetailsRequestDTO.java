package com.serviceplus.metadata.correlationConfig.approval.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class ApprovalDetailsRequestDTO {

    @NotBlank(message = "source can't be blank")
    private String source;

    private Long serviceId;

    private Long setId;

    @NotEmpty(message = "sets can't be empty")
    @Valid
    private List<ApprovalSetRequestDTO> sets = new ArrayList<>();

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getSetId() {
        return setId;
    }

    public void setSetId(Long setId) {
        this.setId = setId;
    }

    public List<ApprovalSetRequestDTO> getSets() {
        return sets;
    }

    public void setSets(List<ApprovalSetRequestDTO> sets) {
        this.sets = sets;
    }

    public static class ApprovalSetRequestDTO {

        private Long setId;

        private List<String> outputSetIds = new ArrayList<>();

        public Long getSetId() {
            return setId;
        }

        public void setSetId(Long setId) {
            this.setId = setId;
        }

        public List<String> getOutputSetIds() {
            return outputSetIds;
        }

        public void setOutputSetIds(List<String> outputSetIds) {
            this.outputSetIds = outputSetIds;
        }
    }
}