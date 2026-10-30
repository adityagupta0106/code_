package com.serviceplus.metadata.advConfiguration.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class AdvConfigurationSaveRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long advConfigId;

    @NotBlank(message = "enrollApplId cannot be blank")
    private String enrollApplId;

    @NotBlank(message = "authKey cannot be blank")
    private String authKey;

    @NotBlank(message = "username cannot be blank")
    private String username;

    private String password;
    private String xApiKey;
    private String authKeyStaging;
    private String usernameStaging;
    private String passwordStaging;
    private String xApiKeyStaging;
    private String authenticateUrlStaging;
    private String tokenizeUrlStaging;
    private String detokenizeUrlStaging;
    private String authenticateUrlProd;
    private String tokenizeUrlProd;
    private String detokenizeUrlProd;
    private Integer serviceId;
    private Integer baseServiceId;

    public AdvConfigurationSaveRequestDTO() {
    }

    public Long getAdvConfigId() {
        return advConfigId;
    }

    public void setAdvConfigId(Long advConfigId) {
        this.advConfigId = advConfigId;
    }

    public String getEnrollApplId() {
        return enrollApplId;
    }

    public void setEnrollApplId(String enrollApplId) {
        this.enrollApplId = enrollApplId;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getxApiKey() {
        return xApiKey;
    }

    public void setxApiKey(String xApiKey) {
        this.xApiKey = xApiKey;
    }

    public String getAuthKeyStaging() {
        return authKeyStaging;
    }

    public void setAuthKeyStaging(String authKeyStaging) {
        this.authKeyStaging = authKeyStaging;
    }

    public String getUsernameStaging() {
        return usernameStaging;
    }

    public void setUsernameStaging(String usernameStaging) {
        this.usernameStaging = usernameStaging;
    }

    public String getPasswordStaging() {
        return passwordStaging;
    }

    public void setPasswordStaging(String passwordStaging) {
        this.passwordStaging = passwordStaging;
    }

    public String getxApiKeyStaging() {
        return xApiKeyStaging;
    }

    public void setxApiKeyStaging(String xApiKeyStaging) {
        this.xApiKeyStaging = xApiKeyStaging;
    }

    public String getAuthenticateUrlStaging() {
        return authenticateUrlStaging;
    }

    public void setAuthenticateUrlStaging(String authenticateUrlStaging) {
        this.authenticateUrlStaging = authenticateUrlStaging;
    }

    public String getTokenizeUrlStaging() {
        return tokenizeUrlStaging;
    }

    public void setTokenizeUrlStaging(String tokenizeUrlStaging) {
        this.tokenizeUrlStaging = tokenizeUrlStaging;
    }

    public String getDetokenizeUrlStaging() {
        return detokenizeUrlStaging;
    }

    public void setDetokenizeUrlStaging(String detokenizeUrlStaging) {
        this.detokenizeUrlStaging = detokenizeUrlStaging;
    }

    public String getAuthenticateUrlProd() {
        return authenticateUrlProd;
    }

    public void setAuthenticateUrlProd(String authenticateUrlProd) {
        this.authenticateUrlProd = authenticateUrlProd;
    }

    public String getTokenizeUrlProd() {
        return tokenizeUrlProd;
    }

    public void setTokenizeUrlProd(String tokenizeUrlProd) {
        this.tokenizeUrlProd = tokenizeUrlProd;
    }

    public String getDetokenizeUrlProd() {
        return detokenizeUrlProd;
    }

    public void setDetokenizeUrlProd(String detokenizeUrlProd) {
        this.detokenizeUrlProd = detokenizeUrlProd;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }
}
