package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;

public class BsaConfigurationResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long departmentId;
    private String tenantId;
    private String departmentName;
    private boolean configured;
    private boolean canGenerate;
    private String serviceCode;
    private Integer stateCode;
    private String bsaUserId;
    private String maskedPassword;
    private String maskedEncryptionKey;
    private String maskedIv;
    private String maskedKeyStaging;
    private String maskedIvStaging;
    private String registrationStatus;
    private String stagingUrl;
    private String prodUrl;
    private String adminEmail;
    private Boolean pdfGenerated;
    private Boolean emailDelivered;
    private String message;
    private Integer baseServiceId;
    private Integer serviceId;
    private String schemeCode;
    private String schemeName;

    public BsaConfigurationResponseDTO() {
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public boolean isCanGenerate() {
        return canGenerate;
    }

    public void setCanGenerate(boolean canGenerate) {
        this.canGenerate = canGenerate;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }

    public String getBsaUserId() {
        return bsaUserId;
    }

    public void setBsaUserId(String bsaUserId) {
        this.bsaUserId = bsaUserId;
    }

    public String getMaskedPassword() {
        return maskedPassword;
    }

    public void setMaskedPassword(String maskedPassword) {
        this.maskedPassword = maskedPassword;
    }

    public String getMaskedEncryptionKey() {
        return maskedEncryptionKey;
    }

    public void setMaskedEncryptionKey(String maskedEncryptionKey) {
        this.maskedEncryptionKey = maskedEncryptionKey;
    }

    public String getMaskedIv() {
        return maskedIv;
    }

    public void setMaskedIv(String maskedIv) {
        this.maskedIv = maskedIv;
    }

    public String getMaskedKeyStaging() {
        return maskedKeyStaging;
    }

    public void setMaskedKeyStaging(String maskedKeyStaging) {
        this.maskedKeyStaging = maskedKeyStaging;
    }

    public String getMaskedIvStaging() {
        return maskedIvStaging;
    }

    public void setMaskedIvStaging(String maskedIvStaging) {
        this.maskedIvStaging = maskedIvStaging;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getStagingUrl() {
        return stagingUrl;
    }

    public void setStagingUrl(String stagingUrl) {
        this.stagingUrl = stagingUrl;
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public void setProdUrl(String prodUrl) {
        this.prodUrl = prodUrl;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public Boolean getPdfGenerated() {
        return pdfGenerated;
    }

    public void setPdfGenerated(Boolean pdfGenerated) {
        this.pdfGenerated = pdfGenerated;
    }

    public Boolean getEmailDelivered() {
        return emailDelivered;
    }

    public void setEmailDelivered(Boolean emailDelivered) {
        this.emailDelivered = emailDelivered;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }
}
