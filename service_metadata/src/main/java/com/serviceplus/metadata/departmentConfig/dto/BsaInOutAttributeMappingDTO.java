package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;

public class BsaInOutAttributeMappingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bsaConfigId;
    private Integer serviceId;
    private Integer baseServiceId;
    private String taskId;
    private String taskName;
    private String attributeId;
    private String attributeLabel;
    private String inOut; // "IN" or "OUT" (or "I" / "O")
    private String nodeRef; // e.g. "uid", "name", "gender", "dob", "beneficiary_id"
    private Integer versionNo;
    private Integer minorVersion;
    private String tenantId;

    public BsaInOutAttributeMappingDTO() {
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeLabel() {
        return attributeLabel;
    }

    public void setAttributeLabel(String attributeLabel) {
        this.attributeLabel = attributeLabel;
    }

    public String getInOut() {
        return inOut;
    }

    public void setInOut(String inOut) {
        this.inOut = inOut;
    }

    public String getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(String nodeRef) {
        this.nodeRef = nodeRef;
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
