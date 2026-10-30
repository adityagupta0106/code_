package com.serviceplus.tracking.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "applicant_inbox",schema = SP_SCHEMA_NAME)
public class ApplicantInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false)
    private String applicationId;

    @Column(name = "application_ref_no")
    private String applicationRefNo;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "form_id")
    private String formId;

    @Column(name = "task_id", nullable = false)
    private String taskId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "current_process_id", nullable = false, unique = true)
    private String currentProcessId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "applied_by")
    private Long appliedBy;

    @Column(name = "beneficiary_name")
    private String beneficiaryName;

    @Column(name = "submission_to_same_official")
    private boolean submissionToSameOfficial;

    @Column(name = "requires_payment")
    private boolean requiresPayment;

    @Column(name = "requires_form")
    private boolean requiresForm;

    @Column(name = "status")
    private String status;

    @Column(name = "apply_date")
    private LocalDateTime applyDate;

    @Column(name = "cr_date")
    private LocalDateTime createdOn;

    @Column(name = "up_date")
    private LocalDateTime modifiedOn;

    transient private Boolean deleteRecord = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationRefNo() {
        return applicationRefNo;
    }

    public void setApplicationRefNo(String applicationRefNo) {
        this.applicationRefNo = applicationRefNo;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
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

    public String getCurrentProcessId() {
        return currentProcessId;
    }

    public void setCurrentProcessId(String currentProcessId) {
        this.currentProcessId = currentProcessId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getAppliedBy() {
        return appliedBy;
    }

    public void setAppliedBy(Long appliedBy) {
        this.appliedBy = appliedBy;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public boolean isSubmissionToSameOfficial() {
        return submissionToSameOfficial;
    }

    public void setSubmissionToSameOfficial(boolean submissionToSameOfficial) {
        this.submissionToSameOfficial = submissionToSameOfficial;
    }

    public boolean isRequiresPayment() {
        return requiresPayment;
    }

    public void setRequiresPayment(boolean requiresPayment) {
        this.requiresPayment = requiresPayment;
    }

    public boolean isRequiresForm() {
        return requiresForm;
    }

    public void setRequiresForm(boolean requiresForm) {
        this.requiresForm = requiresForm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(LocalDateTime applyDate) {
        this.applyDate = applyDate;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Boolean getDeleteRecord() {
        return deleteRecord;
    }

    public void setDeleteRecord(Boolean deleteRecord) {
        this.deleteRecord = deleteRecord;
    }
}