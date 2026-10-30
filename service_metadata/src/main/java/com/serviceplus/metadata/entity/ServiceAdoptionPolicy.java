package com.serviceplus.metadata.entity;

import java.io.Serializable;
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
@Table(name = "service_adoption_policy", schema = SP_SCHEMA_NAME)
public class ServiceAdoptionPolicy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "non_adopted_submission_policy", nullable = false, length = 50)
    private String nonAdoptedSubmissionPolicy; // ALLOW_NON_ADOPTED_SUBMISSION / BLOCK_NON_ADOPTED_SUBMISSION

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks; // Department Order / Policy Reference Remarks

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

    public String getNonAdoptedSubmissionPolicy() {
        return nonAdoptedSubmissionPolicy;
    }

    public void setNonAdoptedSubmissionPolicy(String nonAdoptedSubmissionPolicy) {
        this.nonAdoptedSubmissionPolicy = nonAdoptedSubmissionPolicy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
