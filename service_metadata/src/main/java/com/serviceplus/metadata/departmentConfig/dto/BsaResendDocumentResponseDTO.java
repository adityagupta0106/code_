package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;
import java.util.Date;

public class BsaResendDocumentResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String serviceCode;
    private String adminEmail;
    private String supportDeskEmail;
    private Date sentAt;

    public BsaResendDocumentResponseDTO() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getSupportDeskEmail() {
        return supportDeskEmail;
    }

    public void setSupportDeskEmail(String supportDeskEmail) {
        this.supportDeskEmail = supportDeskEmail;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}
