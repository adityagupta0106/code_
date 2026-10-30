package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BsaServiceMappingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bsaConfigId;
    private String bsaServiceName;
    private String schemeCode;
    private String schemeName;
    private String serviceCode;
    private Integer serviceId;
    private String serviceName;
    private Integer baseServiceId;
    private Integer versionNo;
    private Integer minorVersionNo;
    private String tenantId;
    private Boolean activeStatus;
    private List<BsaInOutAttributeMappingDTO> attributeMappings = new ArrayList<>();
    private Date createdDate;
    private Date updatedDate;

    public BsaServiceMappingDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBsaConfigId() {
        return bsaConfigId;
    }

    public void setBsaConfigId(Long bsaConfigId) {
        this.bsaConfigId = bsaConfigId;
    }

    public String getBsaServiceName() {
        return bsaServiceName;
    }

    public void setBsaServiceName(String bsaServiceName) {
        this.bsaServiceName = bsaServiceName;
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

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
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

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public List<BsaInOutAttributeMappingDTO> getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(List<BsaInOutAttributeMappingDTO> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
