package com.serviceplus.metadata.advConfiguration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "adv_configuration", schema = "schm_sp")
public class AdvConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adv_config_id")
    private Long advConfigId;

    @Column(name = "enroll_appl_id", nullable = false)
    private String enrollApplId;

    @Column(name = "auth_key", nullable = false)
    private String authKey;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "x_api_key")
    private String xApiKey;

    @Column(name = "auth_key_staging")
    private String authKeyStaging;

    @Column(name = "username_staging")
    private String usernameStaging;

    @Column(name = "password_staging")
    private String passwordStaging;

    @Column(name = "x_api_key_staging")
    private String xApiKeyStaging;

    @Column(name = "authenticate_url_staging")
    private String authenticateUrlStaging;

    @Column(name = "tokenize_url_staging")
    private String tokenizeUrlStaging;

    @Column(name = "detokenize_url_staging")
    private String detokenizeUrlStaging;

    @Column(name = "authenticate_url_prod")
    private String authenticateUrlProd;

    @Column(name = "tokenize_url_prod")
    private String tokenizeUrlProd;

    @Column(name = "detokenize_url_prod")
    private String detokenizeUrlProd;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cr_date")
    private Date cDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "up_date")
    private Date uDate;

    @Column(name = "cr_by_user_id")
    private Long cByUserId;

    @Column(name = "up_by_user_id")
    private Long uByUserId;
    
    

    public AdvConfiguration() {
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
