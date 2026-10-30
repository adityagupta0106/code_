package com.serviceplus.metadata.versioning.entity;

import com.serviceplus.metadata.versioning.enums.ServiceComponent;
import jakarta.persistence.*;

import java.util.Date;

import static com.serviceplus.metadata.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "service_version_change_log", schema = SP_SCHEMA_NAME)
public class ServiceVersionChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id", nullable = false)
    private Integer baseServiceId;

    @Column(name = "version_change_id")
    private Long versionChangeId;

    @Column(name = "modification_token")
    private String modificationToken;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "minor_version_no", nullable = false)
    private Integer minorVersionNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "component", nullable = false)
    private ServiceComponent component;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "change_summary", columnDefinition = "text")
    private String changeSummary;

    @Column(name = "previous_config", columnDefinition = "text")
    private String previousConfig;

    @Column(name = "current_config", columnDefinition = "text")
    private String currentConfig;

    @Column(name = "diff_details", columnDefinition = "text")
    private String diffDetails;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getVersionChangeId() {
        return versionChangeId;
    }

    public void setVersionChangeId(Long versionChangeId) {
        this.versionChangeId = versionChangeId;
    }

    public String getModificationToken() {
        return modificationToken;
    }

    public void setModificationToken(String modificationToken) {
        this.modificationToken = modificationToken;
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

    public ServiceComponent getComponent() {
        return component;
    }

    public void setComponent(ServiceComponent component) {
        this.component = component;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    public String getPreviousConfig() {
        return previousConfig;
    }

    public void setPreviousConfig(String previousConfig) {
        this.previousConfig = previousConfig;
    }

    public String getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(String currentConfig) {
        this.currentConfig = currentConfig;
    }

    public String getDiffDetails() {
        return diffDetails;
    }

    public void setDiffDetails(String diffDetails) {
        this.diffDetails = diffDetails;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
