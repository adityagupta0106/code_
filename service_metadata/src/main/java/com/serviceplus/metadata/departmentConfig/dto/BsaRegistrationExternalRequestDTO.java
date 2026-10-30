package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;

public class BsaRegistrationExternalRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long departmentId;
    private String tenantId;
    private Integer stateCode;
    private String departmentName;
    private String bsaUserId;
    private String password;
    private String encryptionKey;
    private String initializationVector;
    private String requestId;
    private String adminEmail;
    private String endpointVersion;
    private String publicKey;

    public BsaRegistrationExternalRequestDTO() {
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

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getBsaUserId() {
        return bsaUserId;
    }

    public void setBsaUserId(String bsaUserId) {
        this.bsaUserId = bsaUserId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(String initializationVector) {
        this.initializationVector = initializationVector;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getEndpointVersion() {
        return endpointVersion;
    }

    public void setEndpointVersion(String endpointVersion) {
        this.endpointVersion = endpointVersion;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "BsaRegistrationExternalRequestDTO{" +
                "departmentId=" + departmentId +
                ", tenantId='" + tenantId + '\'' +
                ", stateCode=" + stateCode +
                ", departmentName='" + departmentName + '\'' +
                ", bsaUserId='" + bsaUserId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", adminEmail='" + adminEmail + '\'' +
                ", endpointVersion='" + endpointVersion + '\'' +
                '}';
    }
}
