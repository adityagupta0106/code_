package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;

public class BsaGenerateConfigurationResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String serviceCode;
    private String registrationStatus;
    private Long departmentId;
    private String tenantId;
    private String bsaUserId;
    private Integer stateCode;
    private String maskedPassword;
    private String maskedEncryptionKey;
    private String maskedIv;
    private String maskedKeyStaging;
    private String maskedIvStaging;
    private boolean pdfGenerated;
    private boolean emailDelivered;
    private String message;

    public BsaGenerateConfigurationResponseDTO() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
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

    public String getBsaUserId() {
        return bsaUserId;
    }

    public void setBsaUserId(String bsaUserId) {
        this.bsaUserId = bsaUserId;
    }

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
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

    public boolean isPdfGenerated() {
        return pdfGenerated;
    }

    public void setPdfGenerated(boolean pdfGenerated) {
        this.pdfGenerated = pdfGenerated;
    }

    public boolean isEmailDelivered() {
        return emailDelivered;
    }

    public void setEmailDelivered(boolean emailDelivered) {
        this.emailDelivered = emailDelivered;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
