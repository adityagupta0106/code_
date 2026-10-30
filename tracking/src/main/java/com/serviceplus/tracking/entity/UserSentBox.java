package com.serviceplus.tracking.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import io.lettuce.core.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "user_sentbox", schema = SP_SCHEMA_NAME)
public class UserSentBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "process_id")
    private String processId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "appl_id")
    private String applId;

    @Column(name = "appl_ref_no")
    private String applRefNo;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "calledback")
    private Boolean calledback;

    @Column(name = "holder_id")
    private String holderId;

    @Column(name = "action_code")
    private Integer actionCode;

    @Column(name = "action_user_id")
    private Integer actionUserId;

    @Column(name = "action_on")
    private LocalDateTime actionOn;

    @Column(name = "action_on_date")
    private LocalDateTime actionOnDate;

    @Column(name = "parent_ref_no")
    private String parentRefNo;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;

    @Column(name = "tenant_id")
    private String tenantId;

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
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

    public String getApplId() {
        return applId;
    }

    public void setApplId(String applId) {
        this.applId = applId;
    }

    public String getApplRefNo() {
        return applRefNo;
    }

    public void setApplRefNo(String applRefNo) {
        this.applRefNo = applRefNo;
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

    public Boolean getCalledback() {
        return calledback;
    }

    public void setCalledback(Boolean calledback) {
        this.calledback = calledback;
    }

    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
    }

    public Integer getActionCode() {
        return actionCode;
    }

    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
    }

    public Integer getActionUserId() {
        return actionUserId;
    }

    public void setActionUserId(Integer actionUserId) {
        this.actionUserId = actionUserId;
    }

    public LocalDateTime getActionOn() {
        return actionOn;
    }

    public void setActionOn(LocalDateTime actionOn) {
        this.actionOn = actionOn;
    }

    public String getParentRefNo() {
        return parentRefNo;
    }

    public void setParentRefNo(String parentRefNo) {
        this.parentRefNo = parentRefNo;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getActionOnDate() {
        return actionOnDate;
    }

    public void setActionOnDate(LocalDateTime actionOnDate) {
        this.actionOnDate = actionOnDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}