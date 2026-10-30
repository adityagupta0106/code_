package com.serviceplus.metadata.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Date;

import static com.serviceplus.metadata.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "workflow_assignment_history",schema = SP_SCHEMA_NAME)
public class WorkflowAssignmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "holder_id")
    private String holderId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "assigned_by")
    private Long assignedBy;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "crt_on")
    private Date crtOn;

    @Column(name = "upd_on")
    private Date updOn;

    @Column(name = "tenant_id")
    private String tenantId;

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Date getCrtOn() {
        return crtOn;
    }

    public void setCrtOn(Date crtOn) {
        this.crtOn = crtOn;
    }

    public Date getUpdOn() {
        return updOn;
    }

    public void setUpdOn(Date updOn) {
        this.updOn = updOn;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
