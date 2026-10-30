package com.serviceplus.tracking.entity;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "application_tracking", schema = SP_SCHEMA_NAME)
public class ApplicationTracking {

    @Id
    @Column(name = "process_id")
    private String processId;

    @Column(name = "appl_id")
    private String applId;

    @Column(name = "appl_ref_no")
    private String applRefNo;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "previous_task_id")
    private String previousTaskId;

    @Column(name = "previous_task_name")
    private String previousTaskName;

    @Column(name = "action_code")
    private Integer actionCode;

    @Column(name = "action_name")
    private String actionName;

    @Column(name = "action_taken")
    private String actionTaken;

    @Column(name = "applied_user_id")
    private Long appliedUserId;

    @Column(name = "beneficiary_user_name")
    private String beneficiaryUserName;

    @Column(name = "apply_date")
    private LocalDateTime applyDate;

    @Column(name = "holder_id")
    private String holderId;

    @Column(name = "form_id")
    private String formId;

    @Column(name = "data_id")
    private String dataId;

    @Column(name = "action_on")
    private LocalDateTime actionOn;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "created_on", insertable = false, updatable = false)
    private LocalDateTime createdOn;

    @OneToMany(
            mappedBy = "tracking",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ApplicationTrackingDocument> documents =  new ArrayList<>();

    @Column(name = "action_remarks")
    private String actionRemarks;
    
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
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

    public Integer getActionCode() {
        return actionCode;
    }

    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public Long getAppliedUserId() {
        return appliedUserId;
    }

    public void setAppliedUserId(Long appliedUserId) {
        this.appliedUserId = appliedUserId;
    }

    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public LocalDateTime getActionOn() {
        return actionOn;
    }

    public void setActionOn(LocalDateTime actionOn) {
        this.actionOn = actionOn;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public List<ApplicationTrackingDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ApplicationTrackingDocument> documents) {
        this.documents = documents;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getBeneficiaryUserName() {
        return beneficiaryUserName;
    }

    public void setBeneficiaryUserName(String beneficiaryUserName) {
        this.beneficiaryUserName = beneficiaryUserName;
    }

    public LocalDateTime getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(LocalDateTime applyDate) {
        this.applyDate = applyDate;
    }

    public String getPreviousTaskId() {
        return previousTaskId;
    }

    public void setPreviousTaskId(String previousTaskId) {
        this.previousTaskId = previousTaskId;
    }

    public String getPreviousTaskName() {
        return previousTaskName;
    }

    public void setPreviousTaskName(String previousTaskName) {
        this.previousTaskName = previousTaskName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
    
    public String getActionRemarks() {
        return actionRemarks;
    }

    public void setActionRemarks(String actionRemarks) {
        this.actionRemarks = actionRemarks;
    }
}