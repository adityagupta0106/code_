package com.serviceplus.metadata.esign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.dto.LabelValue;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsignTemplateResponseDTO {

    private Long templateId;
    private String templateName;
    private String status;
    EsignTemplateJSONDTO templateJson;

    public EsignTemplateResponseDTO() {
    }

    public EsignTemplateJSONDTO getTemplateJson() {
        return templateJson;
    }

    public void setTemplateJson(EsignTemplateJSONDTO templateJson) {
        this.templateJson = templateJson;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
