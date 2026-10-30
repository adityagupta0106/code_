package com.serviceplus.metadata.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import static com.serviceplus.metadata.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "service_submission_location_adoption", schema = SP_SCHEMA_NAME)
public class ServiceSubmissionLocationAdoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id", nullable = false)
    private Integer baseServiceId;

    @Column(name = "sdu_level_id", nullable = false)
    private Integer sduLevelId;

    @Column(name = "sdu_location_id", nullable = false)
    private Integer sduLocationId;

    @Column(name = "submission_level_id", nullable = false)
    private Integer submissionLevelId;

    @Column(name = "submission_location_id", nullable = false)
    private Integer submissionLocationId;

    @Column(name = "submission_location_name")
    private String submissionLocationName;

    @Column(name = "adoption_status", nullable = false, length = 30)
    private String adoptionStatus;

    @Column(name = "adopted_by")
    private Long adoptedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "adopted_on")
    private Date adoptedOn;

    @Column(name = "suspended_by")
    private Long suspendedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "suspended_on")
    private Date suspendedOn;

    @Column(name = "suspension_remarks")
    private String suspensionRemarks;

    @Column(name = "reactivated_by")
    private Long reactivatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reactivated_on")
    private Date reactivatedOn;

    @Column(name = "designated_officer_user_id")
    private Long designatedOfficerUserId;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getSduLevelId() {
        return sduLevelId;
    }

    public void setSduLevelId(Integer sduLevelId) {
        this.sduLevelId = sduLevelId;
    }

    public Integer getSduLocationId() {
        return sduLocationId;
    }

    public void setSduLocationId(Integer sduLocationId) {
        this.sduLocationId = sduLocationId;
    }

    public Integer getSubmissionLevelId() {
        return submissionLevelId;
    }

    public void setSubmissionLevelId(Integer submissionLevelId) {
        this.submissionLevelId = submissionLevelId;
    }

    public Integer getSubmissionLocationId() {
        return submissionLocationId;
    }

    public void setSubmissionLocationId(Integer submissionLocationId) {
        this.submissionLocationId = submissionLocationId;
    }

    public String getSubmissionLocationName() {
        return submissionLocationName;
    }

    public void setSubmissionLocationName(String submissionLocationName) {
        this.submissionLocationName = submissionLocationName;
    }

    public String getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(String adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }

    public Long getAdoptedBy() {
        return adoptedBy;
    }

    public void setAdoptedBy(Long adoptedBy) {
        this.adoptedBy = adoptedBy;
    }

    public Date getAdoptedOn() {
        return adoptedOn;
    }

    public void setAdoptedOn(Date adoptedOn) {
        this.adoptedOn = adoptedOn;
    }

    public Long getSuspendedBy() {
        return suspendedBy;
    }

    public void setSuspendedBy(Long suspendedBy) {
        this.suspendedBy = suspendedBy;
    }

    public Date getSuspendedOn() {
        return suspendedOn;
    }

    public void setSuspendedOn(Date suspendedOn) {
        this.suspendedOn = suspendedOn;
    }

    public String getSuspensionRemarks() {
        return suspensionRemarks;
    }

    public void setSuspensionRemarks(String suspensionRemarks) {
        this.suspensionRemarks = suspensionRemarks;
    }

    public Long getReactivatedBy() {
        return reactivatedBy;
    }

    public void setReactivatedBy(Long reactivatedBy) {
        this.reactivatedBy = reactivatedBy;
    }

    public Date getReactivatedOn() {
        return reactivatedOn;
    }

    public void setReactivatedOn(Date reactivatedOn) {
        this.reactivatedOn = reactivatedOn;
    }

    public Long getDesignatedOfficerUserId() {
        return designatedOfficerUserId;
    }

    public void setDesignatedOfficerUserId(Long designatedOfficerUserId) {
        this.designatedOfficerUserId = designatedOfficerUserId;
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
