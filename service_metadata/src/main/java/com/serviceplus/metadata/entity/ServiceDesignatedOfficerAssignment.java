package com.serviceplus.metadata.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import static com.serviceplus.metadata.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "service_designated_officer_assignment",schema = SP_SCHEMA_NAME)
public class ServiceDesignatedOfficerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "service_id")
    private Integer serviceId;
    
    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "delivery_unit_level_id")
    private Integer deliveryUnitLevelId;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "designation_id")
    private Integer designationId;

    @Column(name = "cr_by")
    private Long createdBy;

    @Column(name = "c_date")
    private Date createdOn;

    @Column(name = "up_by")
    private Long modifiedBy;

    @Column(name = "u_date")
    private Date modifiedOn;

    @Column(name = "deleted_flag")
    private Boolean deletedFlag = false;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

	public Long getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Long assignmentId) {
		this.assignmentId = assignmentId;
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

	public Integer getDeliveryUnitLevelId() {
		return deliveryUnitLevelId;
	}

	public void setDeliveryUnitLevelId(Integer deliveryUnitLevelId) {
		this.deliveryUnitLevelId = deliveryUnitLevelId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getDesignationId() {
		return designationId;
	}

	public void setDesignationId(Integer designationId) {
		this.designationId = designationId;
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
