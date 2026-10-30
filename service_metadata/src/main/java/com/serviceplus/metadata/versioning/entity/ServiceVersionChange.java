package com.serviceplus.metadata.versioning.entity;

import com.serviceplus.metadata.versioning.enums.ModificationStatus;
import com.serviceplus.metadata.versioning.enums.VersionType;
import jakarta.persistence.*;

import java.util.Date;

import static com.serviceplus.metadata.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "service_version_change", schema = SP_SCHEMA_NAME)
public class ServiceVersionChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id", nullable = false)
    private Integer baseServiceId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "current_version_no", nullable = false)
    private Integer currentVersionNo;

    @Column(name = "current_minor_version_no", nullable = false)
    private Integer currentMinorVersionNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_version_type", nullable = false)
    private VersionType requestedVersionType;

    @Column(name = "target_service_id", nullable = false)
    private Integer targetServiceId;

    @Column(name = "target_version_no", nullable = false)
    private Integer targetVersionNo;

    @Column(name = "target_minor_version_no", nullable = false)
    private Integer targetMinorVersionNo;

    @Column(name = "modification_token", nullable = false, unique = true)
    private String modificationToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ModificationStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public Long getId() {
        return id;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCurrentVersionNo() {
        return currentVersionNo;
    }

    public void setCurrentVersionNo(Integer currentVersionNo) {
        this.currentVersionNo = currentVersionNo;
    }

    public Integer getCurrentMinorVersionNo() {
        return currentMinorVersionNo;
    }

    public void setCurrentMinorVersionNo(Integer currentMinorVersionNo) {
        this.currentMinorVersionNo = currentMinorVersionNo;
    }

    public VersionType getRequestedVersionType() {
        return requestedVersionType;
    }

    public void setRequestedVersionType(VersionType requestedVersionType) {
        this.requestedVersionType = requestedVersionType;
    }

    public Integer getTargetServiceId() {
        return targetServiceId;
    }

    public void setTargetServiceId(Integer targetServiceId) {
        this.targetServiceId = targetServiceId;
    }

    public Integer getTargetVersionNo() {
        return targetVersionNo;
    }

    public void setTargetVersionNo(Integer targetVersionNo) {
        this.targetVersionNo = targetVersionNo;
    }

    public Integer getTargetMinorVersionNo() {
        return targetMinorVersionNo;
    }

    public void setTargetMinorVersionNo(Integer targetMinorVersionNo) {
        this.targetMinorVersionNo = targetMinorVersionNo;
    }

    public String getModificationToken() {
        return modificationToken;
    }

    public void setModificationToken(String modificationToken) {
        this.modificationToken = modificationToken;
    }

    public ModificationStatus getStatus() {
        return status;
    }

    public void setStatus(ModificationStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}