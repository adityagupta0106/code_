package com.serviceplus.metadata.departmentConfig.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bsa_configuration", schema = "schm_sp")
public class BsaConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "entity_id")
    private Integer entityId;
    
    @Column(name = "scheme_code")
    private String schemeCode;

    @Column(name = "scheme_name")
    private String schemeName;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "state_code")
    private Integer sCode;

    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "bsa_staging_url")
    private String stagingUrl;

    @Column(name = "bsa_prod_url")
    private String prodUrl;
    
    @Column(name = "bsa_user_name_staging", nullable = false)
    private String userNameStaging;

    @Column(name = "bsa_password_staging", nullable = false)
    private String passwordStaging;

    @Column(name = "bsa_user_name_prod", nullable = false)
    private String userNameProd;

    @Column(name = "bsa_password_prod", nullable = false)
    private String passwordProd;

    @Column(name = "bsa_key_prod", nullable = false)
    private String keyProd;

    @Column(name = "bsa_iv_prod", nullable = false)
    private String ivProd;

    @Column(name = "bsa_key_staging")
    private String keyStaging;

    @Column(name = "bsa_iv_staging")
    private String ivStaging;

    @Column(name = "active_status")
    private Boolean activeStatus = true;

    @Column(name = "cr_by")
    private Long configBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cr_on")
    private Date configCreatedOn = new Date();

    @Column(name = "up_by")
    private Long modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "up_on")
    private Date modifiedOn = new Date();

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "auth_type")
    private String authType = "eKYC";

    @Column(name = "version_no")
    private Integer versionNo = 1;

    @Column(name = "minor_version_no")
    private Integer minorVersion = 0;
    
    @Column(name="tenant_id")
    private String tenantId;

    public BsaConfiguration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
    public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
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

    public Integer getsCode() {
        return sCode;
    }

    public void setsCode(Integer sCode) {
        this.sCode = sCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
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

	public String getUserNameProd() {
        return userNameProd;
    }

    public void setUserNameProd(String userNameProd) {
        this.userNameProd = userNameProd;
    }

    public String getPasswordProd() {
        return passwordProd;
    }

    public void setPasswordProd(String passwordProd) {
        this.passwordProd = passwordProd;
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

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Date getConfigCreatedOn() {
        return configCreatedOn;
    }

    public void setConfigCreatedOn(Date configCreatedOn) {
        this.configCreatedOn = configCreatedOn;
    }

    public Long getConfigBy() {
		return configBy;
	}

	public void setConfigBy(Long configBy) {
		this.configBy = configBy;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
