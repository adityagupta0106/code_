package com.serviceplus.metadata.departmentConfig.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class BsaRegistrationExternalResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String message;

    @JsonProperty("service_code")
    @JsonAlias({"serviceCode", "service_code"})
    private String serviceCode;

    private String registrationStatus;
    private String requestId;
    private String errorCode;

    @JsonProperty("bsa_staging_url")
    @JsonAlias({"bsaStagingUrl", "stagingUrl", "bsa_staging_url"})
    private String bsaStagingUrl;

    @JsonProperty("bsa_url")
    @JsonAlias({"bsaUrl", "prodUrl", "bsa_prod_url", "bsa_url"})
    private String bsaUrl;

    @JsonProperty("user_name_staging")
    @JsonAlias({"userNameStaging", "user_name_staging", "bsa_user_name_staging"})
    private String userNameStaging;

    @JsonProperty("password_staging")
    @JsonAlias({"passwordStaging", "password_staging", "bsa_password_staging"})
    private String passwordStaging;
        
    @JsonProperty("user_name")
    @JsonAlias({"userName", "bsaUserId", "user_name"})
    private String userName;
    
    @JsonProperty("password")
    @JsonAlias({"password", "passwordProd", "bsa_password_prod"})
    private String password;

    @JsonProperty("key_iv")
    @JsonAlias({"keyIv", "key_iv"})
    private String keyIv;

    @JsonProperty("key_prod")
    @JsonAlias({"keyProd", "key_prod", "encryptionKey"})
    private String keyProd;

    @JsonProperty("iv_prod")
    @JsonAlias({"ivProd", "iv_prod", "initializationVector"})
    private String ivProd;

    @JsonProperty("key_staging")
    @JsonAlias({"keyStaging", "key_staging", "bsa_key_staging", "stagingKey"})
    private String keyStaging;

    @JsonProperty("iv_staging")
    @JsonAlias({"ivStaging", "iv_staging", "bsa_iv_staging", "stagingIv"})
    private String ivStaging;

    public BsaRegistrationExternalResponseDTO() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }

    public void setSuccess(Boolean success) {
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

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getBsaStagingUrl() {
        return bsaStagingUrl;
    }

    public void setBsaStagingUrl(String bsaStagingUrl) {
        this.bsaStagingUrl = bsaStagingUrl;
    }

    public String getBsaUrl() {
        return bsaUrl;
    }

    public void setBsaUrl(String bsaUrl) {
        this.bsaUrl = bsaUrl;
    }

    public String getUserNameStaging() {
		return userNameStaging;
	}

	public void setUserNameStaging(String userNameStaging) {
		this.userNameStaging = userNameStaging;
	}

	public String getPasswordStaging() {
		return passwordStaging;
	}

	public void setPasswordStaging(String passwordStaging) {
		this.passwordStaging = passwordStaging;
	}

	public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeyIv() {
        return keyIv;
    }

    public void setKeyIv(String keyIv) {
        this.keyIv = keyIv;
    }

    public String getKeyProd() {
        return keyProd;
    }

    public void setKeyProd(String keyProd) {
        this.keyProd = keyProd;
    }

    public String getIvProd() {
        return ivProd;
    }

    public void setIvProd(String ivProd) {
        this.ivProd = ivProd;
    }

    public String getKeyStaging() {
        return keyStaging;
    }

    public void setKeyStaging(String keyStaging) {
        this.keyStaging = keyStaging;
    }

    public String getIvStaging() {
        return ivStaging;
    }

    public void setIvStaging(String ivStaging) {
        this.ivStaging = ivStaging;
    }

    @Override
    public String toString() {
        // Safe toString: never print passwords or keys
        return "BsaRegistrationExternalResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", registrationStatus='" + registrationStatus + '\'' +
                ", requestId='" + requestId + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", bsaStagingUrl='" + bsaStagingUrl + '\'' +
                ", bsaUrl='" + bsaUrl + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
