package com.serviceplus.metadata.departmentConfig.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bsa_in_out_attribute_mapping", schema = "schm_sp")
public class BsaInOutAttributeMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bsa_config_id", nullable = false)
    private Long bsaConfigId;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "in_out", nullable = false)
    private String inOut;

    @Column(name = "node_ref")
    private String nodeRef;

    @Column(name = "attribute_id")
    private String attributeId;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "version_no")
    private Integer versionNo = 1;

    @Column(name = "minor_version_no")
    private Integer minorVersion = 0;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cr_date")
    private Date createdDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "up_date")
    private Date updatedDate = new Date();

    public BsaInOutAttributeMapping() {
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

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
