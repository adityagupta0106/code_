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
@Table(name = "service_delivery_units", schema = SP_SCHEMA_NAME)
public class ServiceDeliveryUnits {

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

    @Column(name = "delivery_unit_level_id", nullable = false)
    private Integer deliveryUnitLevelId;

    @Column(name = "delivery_unit_location_id")
    private Integer locationId;
    
    @Column(name = "delivery_unit_location_name")
    private String locationName;

    @Column(name = "activation_status")
    private Integer activationStatus;

    @Column(name = "activated_by")
    private Long activatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "activated_on")
    private Date activatedOn;

    @Column(name = "deactivated_by")
    private Long deactivatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivated_on")
    private Date deactivatedOn;

    @Column(name = "remarks")
    private String remarks;

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
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Integer getActivationStatus() {
		return activationStatus;
	}

	public void setActivationStatus(Integer activationStatus) {
		this.activationStatus = activationStatus;
	}

	public Long getActivatedBy() {
		return activatedBy;
	}

	public void setActivatedBy(Long activatedBy) {
		this.activatedBy = activatedBy;
	}

	public Date getActivatedOn() {
		return activatedOn;
	}

	public void setActivatedOn(Date activatedOn) {
		this.activatedOn = activatedOn;
	}

	public Long getDeactivatedBy() {
		return deactivatedBy;
	}

	public void setDeactivatedBy(Long deactivatedBy) {
		this.deactivatedBy = deactivatedBy;
	}

	public Date getDeactivatedOn() {
		return deactivatedOn;
	}

	public void setDeactivatedOn(Date deactivatedOn) {
		this.deactivatedOn = deactivatedOn;
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
