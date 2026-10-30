package com.serviceplus.metadata.advConfiguration.dto;

import com.serviceplus.metadata.advConfiguration.entity.AdvConfiguration;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

public class AdvConfigurationDTO implements Serializable {

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
    private Date cDate;
    private Date uDate;
    private Long cByUserId;
    private Long uByUserId;

    public AdvConfigurationDTO() {
    }

    public static AdvConfigurationDTO fromEntity(AdvConfiguration entity) {
        if (entity == null) {
            return null;
        }
        AdvConfigurationDTO dto = new AdvConfigurationDTO();
        dto.setAdvConfigId(entity.getAdvConfigId());
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
        dto.setServiceId(entity.getServiceId());
        dto.setBaseServiceId(entity.getBaseServiceId());
        dto.setCDate(entity.getCDate());
        dto.setUDate(entity.getUDate());
        dto.setCByUserId(entity.getCByUserId());
        dto.setUByUserId(entity.getUByUserId());
        return dto;
    }

    public AdvConfiguration toEntity() {
        AdvConfiguration entity = new AdvConfiguration();
        entity.setAdvConfigId(this.advConfigId);
        entity.setEnrollApplId(this.enrollApplId);
        entity.setAuthKey(this.authKey);
        entity.setUsername(this.username);
        entity.setPassword(this.password);
        entity.setxApiKey(this.xApiKey);
        entity.setAuthKeyStaging(this.authKeyStaging);
        entity.setUsernameStaging(this.usernameStaging);
        entity.setPasswordStaging(this.passwordStaging);
        entity.setxApiKeyStaging(this.xApiKeyStaging);
        entity.setAuthenticateUrlStaging(this.authenticateUrlStaging);
        entity.setTokenizeUrlStaging(this.tokenizeUrlStaging);
        entity.setDetokenizeUrlStaging(this.detokenizeUrlStaging);
        entity.setAuthenticateUrlProd(this.authenticateUrlProd);
        entity.setTokenizeUrlProd(this.tokenizeUrlProd);
        entity.setDetokenizeUrlProd(this.detokenizeUrlProd);
        entity.setServiceId(this.serviceId);
        entity.setBaseServiceId(this.baseServiceId);
        entity.setCDate(this.cDate);
        entity.setUDate(this.uDate);
        entity.setCByUserId(this.cByUserId);
        entity.setUByUserId(this.uByUserId);
        return entity;
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

    public Date getCDate() {
        return cDate;
    }

    public void setCDate(Date cDate) {
        this.cDate = cDate;
    }

    public Date getUDate() {
        return uDate;
    }

    public void setUDate(Date uDate) {
        this.uDate = uDate;
    }

    public Long getCByUserId() {
        return cByUserId;
    }

    public void setCByUserId(Long cByUserId) {
        this.cByUserId = cByUserId;
    }

    public Long getUByUserId() {
        return uByUserId;
    }

    public void setUByUserId(Long uByUserId) {
        this.uByUserId = uByUserId;
    }
}
