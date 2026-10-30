package com.serviceplus.metadata.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import static com.serviceplus.metadata.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "pending_adoption_application", schema = SP_SCHEMA_NAME)
public class PendingAdoptionApplication {

    @Id
    @Column(name = "application_id", nullable = false, length = 100)
    private String applicationId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id", nullable = false)
    private Integer baseServiceId;

    @Column(name = "sdu_location_id", nullable = false)
    private Integer sduLocationId;

    @Column(name = "submission_location_id", nullable = false)
    private Integer submissionLocationId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "sla_start_policy", length = 30)
    private String slaStartPolicy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submission_date")
    private Date submissionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "adoption_date")
    private Date adoptionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "released_date")
    private Date releasedDate;

    @Column(name = "citizen_user_id")
    private Long citizenUserId;

    @Column(name = "acknowledgement_number", length = 100)
    private String acknowledgementNumber;

    @Column(name = "first_notification_sent", nullable = false)
    private Boolean firstNotificationSent = false;

    @Column(name = "reminder_count", nullable = false)
    private Integer reminderCount = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_reminder_date")
    private Date lastReminderDate;

    @Column(name = "cr_by", nullable = false)
    private Long createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "c_date", nullable = false)
    private Date createdOn;

    @Column(name = "up_by")
    private Long modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "u_date")
    private Date modifiedOn;

    @Column(name = "deleted_flag", nullable = false)
    private Boolean deletedFlag = false;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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

    public Integer getSduLocationId() {
        return sduLocationId;
    }

    public void setSduLocationId(Integer sduLocationId) {
        this.sduLocationId = sduLocationId;
    }

    public Integer getSubmissionLocationId() {
        return submissionLocationId;
    }

    public void setSubmissionLocationId(Integer submissionLocationId) {
        this.submissionLocationId = submissionLocationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSlaStartPolicy() {
        return slaStartPolicy;
    }

    public void setSlaStartPolicy(String slaStartPolicy) {
        this.slaStartPolicy = slaStartPolicy;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Date getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(Date adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public Date getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(Date releasedDate) {
        this.releasedDate = releasedDate;
    }

    public Long getCitizenUserId() {
        return citizenUserId;
    }

    public void setCitizenUserId(Long citizenUserId) {
        this.citizenUserId = citizenUserId;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public void setAcknowledgementNumber(String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }

    public Boolean getFirstNotificationSent() {
        return firstNotificationSent;
    }

    public void setFirstNotificationSent(Boolean firstNotificationSent) {
        this.firstNotificationSent = firstNotificationSent;
    }

    public Integer getReminderCount() {
        return reminderCount;
    }

    public void setReminderCount(Integer reminderCount) {
        this.reminderCount = reminderCount;
    }

    public Date getLastReminderDate() {
        return lastReminderDate;
    }

    public void setLastReminderDate(Date lastReminderDate) {
        this.lastReminderDate = lastReminderDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Boolean getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
}
