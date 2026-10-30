package com.serviceplus.metadata.esign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EsignTemplateStatusRequestDTO {

    @NotNull(message = "Template ID is mandatory")
    private Long templateId;

    @NotBlank(message = "Status is mandatory (ACTIVE or INACTIVE)")
    private String status;

    public EsignTemplateStatusRequestDTO() {
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
