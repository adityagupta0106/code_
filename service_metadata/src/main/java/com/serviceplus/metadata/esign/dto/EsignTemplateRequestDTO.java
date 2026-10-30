package com.serviceplus.metadata.esign.dto;

import com.serviceplus.metadata.dto.LabelValue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EsignTemplateRequestDTO {

    private Long templateId;

    private LabelValue templateName;

    private String aspId;

    private Boolean hasAppId;

    private String appId;

    private String pfxPassword;

    private EsignTemplateJSONDTO templateJson;

    public EsignTemplateRequestDTO() {
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public LabelValue getTemplateName() {
		return templateName;
	}

	public void setTemplateName(LabelValue templateName) {
		this.templateName = templateName;
	}

	public String getAspId() {
        if (aspId != null && !aspId.trim().isEmpty()) {
            return aspId;
        }
        return templateJson != null ? templateJson.getAspId() : null;
    }

    public void setAspId(String aspId) {
        this.aspId = aspId;
    }

    public Boolean getHasAppId() {
        if (hasAppId != null) {
            return hasAppId;
        }
        return templateJson != null ? templateJson.getHasAppId() : null;
    }

    public void setHasAppId(Boolean hasAppId) {
        this.hasAppId = hasAppId;
    }

    public String getAppId() {
        if (appId != null && !appId.trim().isEmpty()) {
            return appId;
        }
        return templateJson != null ? templateJson.getAppId() : null;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPfxPassword() {
        return pfxPassword;
    }

    public void setPfxPassword(String pfxPassword) {
        this.pfxPassword = pfxPassword;
    }

    public EsignTemplateJSONDTO getTemplateJson() {
        return templateJson;
    }

    public void setTemplateJson(EsignTemplateJSONDTO templateJson) {
        this.templateJson = templateJson;
    }
}
