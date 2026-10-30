package com.serviceplus.metadata.esign.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.dto.LabelValue;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsignTemplateJSONDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LabelValue templateName;
    private String aspId;
    private Boolean hasAppId = false;
    private String appId;
    private String pfxStorageReference;
    private String pfxFilename;
    private Long pfxFileSize;
    private String pfxFingerprint;
    @JsonIgnore
    private LocalDateTime certificateExpiryDate;
    private String certificateSubjectDn;
    private String certificateIssuerDn;
    private String pfxPassword;

    public EsignTemplateJSONDTO() {
    }

    public LabelValue getTemplateName() {
		return templateName;
	}

	public void setTemplateName(LabelValue templateName) {
		this.templateName = templateName;
	}

	public String getAspId() {
        return aspId;
    }

    public void setAspId(String aspId) {
        this.aspId = aspId;
    }

    public Boolean getHasAppId() {
        return hasAppId;
    }

    public void setHasAppId(Boolean hasAppId) {
        this.hasAppId = hasAppId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPfxStorageReference() {
        return pfxStorageReference;
    }

    public void setPfxStorageReference(String pfxStorageReference) {
        this.pfxStorageReference = pfxStorageReference;
    }

    public String getPfxFilename() {
        return pfxFilename;
    }

    public void setPfxFilename(String pfxFilename) {
        this.pfxFilename = pfxFilename;
    }

    public Long getPfxFileSize() {
        return pfxFileSize;
    }

    public void setPfxFileSize(Long pfxFileSize) {
        this.pfxFileSize = pfxFileSize;
    }

    public String getPfxFingerprint() {
        return pfxFingerprint;
    }

    public void setPfxFingerprint(String pfxFingerprint) {
        this.pfxFingerprint = pfxFingerprint;
    }

    public LocalDateTime getCertificateExpiryDate() {
        return certificateExpiryDate;
    }

    public void setCertificateExpiryDate(LocalDateTime certificateExpiryDate) {
        this.certificateExpiryDate = certificateExpiryDate;
    }

    public String getCertificateSubjectDn() {
        return certificateSubjectDn;
    }

    public void setCertificateSubjectDn(String certificateSubjectDn) {
        this.certificateSubjectDn = certificateSubjectDn;
    }

    public String getCertificateIssuerDn() {
        return certificateIssuerDn;
    }

    public void setCertificateIssuerDn(String certificateIssuerDn) {
        this.certificateIssuerDn = certificateIssuerDn;
    }

	public String getPfxPassword() {
		return pfxPassword;
	}

	public void setPfxPassword(String pfxPassword) {
		this.pfxPassword = pfxPassword;
	}
}
