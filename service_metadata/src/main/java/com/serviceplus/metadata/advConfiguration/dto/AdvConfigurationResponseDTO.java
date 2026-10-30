package com.serviceplus.metadata.advConfiguration.dto;

import com.serviceplus.metadata.advConfiguration.entity.AdvConfiguration;

import java.io.Serializable;
import java.util.Date;

public class AdvConfigurationResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long advConfigId;
    private String enrollApplId;
    private String authKey;
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

    public AdvConfigurationResponseDTO() {
    }

    public static AdvConfigurationResponseDTO fromEntity(AdvConfiguration entity) {
        if (entity == null) {
            return null;
        }
        AdvConfigurationResponseDTO dto = new AdvConfigurationResponseDTO();
        dto.setEnrollApplId(entity.getEnrollApplId());
        dto.setAuthKey(entity.getAuthKey());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setxApiKey(entity.getxApiKey());
        dto.setAuthKeyStaging(entity.getAuthKeyStaging());
        dto.setUsernameStaging(entity.getUsernameStaging());
        dto.setPasswordStaging(entity.getPasswordStaging());
        dto.setxApiKeyStaging(entity.getxApiKeyStaging());
        dto.setAuthenticateUrlStaging(entity.getAuthenticateUrlStaging());
        dto.setTokenizeUrlStaging(entity.getTokenizeUrlStaging());
        dto.setDetokenizeUrlStaging(entity.getDetokenizeUrlStaging());
        dto.setAuthenticateUrlProd(entity.getAuthenticateUrlProd());
        dto.setTokenizeUrlProd(entity.getTokenizeUrlProd());
        dto.setDetokenizeUrlProd(entity.getDetokenizeUrlProd());
        return dto;
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
}
