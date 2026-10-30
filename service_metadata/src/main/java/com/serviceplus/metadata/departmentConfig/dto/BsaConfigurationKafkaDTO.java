package com.serviceplus.metadata.departmentConfig.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BsaConfigurationKafkaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventType; // "FREEZE" or "UNFREEZE"
    private Integer serviceId;
    private Integer baseServiceId;
    private String tenantId;

    private BsaConfigPayload bsaConfiguration;
    private BsaServiceMappingDTO serviceMapping;
    private List<BsaInOutAttributeMappingDTO> attributeMappings = new ArrayList<>();

    public BsaConfigurationKafkaDTO() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public BsaConfigPayload getBsaConfiguration() {
        return bsaConfiguration;
    }

    public void setBsaConfiguration(BsaConfigPayload bsaConfiguration) {
        this.bsaConfiguration = bsaConfiguration;
    }

    public BsaServiceMappingDTO getServiceMapping() {
        return serviceMapping;
    }

    public void setServiceMapping(BsaServiceMappingDTO serviceMapping) {
        this.serviceMapping = serviceMapping;
    }

    public List<BsaInOutAttributeMappingDTO> getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(List<BsaInOutAttributeMappingDTO> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }

    public static class BsaConfigPayload implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private Integer baseServiceId;
        private Integer serviceId;
        private String schemeCode;
        private String schemeName;
        private String userName;
        private String password;
        private Integer stateCode;
        private String serviceCode;
        private String stagingUrl;
        private String prodUrl;
        private String userNameProd;
        private String passwordProd;
        private String keyProd;
        private String ivProd;
        private String userNameStaging;
        private String passwordStaging;
        private String keyStaging;
        private String ivStaging;
        private Boolean activeStatus;
        private String authType;
        private Integer versionNo;
        private Integer minorVersionNo;
        private String tenantId;

        public BsaConfigPayload() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public Integer getStateCode() {
            return stateCode;
        }

        public void setStateCode(Integer stateCode) {
            this.stateCode = stateCode;
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

        public Integer getMinorVersionNo() {
            return minorVersionNo;
        }

        public void setMinorVersionNo(Integer minorVersionNo) {
            this.minorVersionNo = minorVersionNo;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }
    }
}
