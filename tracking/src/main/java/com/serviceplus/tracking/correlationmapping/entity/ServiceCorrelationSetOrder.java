package com.serviceplus.tracking.correlationmapping.entity;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

@Entity
@Table(name = "service_correlation_set_order", schema = SP_SCHEMA_NAME)
public class ServiceCorrelationSetOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long baseServiceId;

	private Long serviceId;

	private Long setId;

	private String attrId;

	private Integer attrSortOrder;

	private String attrOrderType;

	private String tenantId;

	private Integer entityId;

	private OffsetDateTime crDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Long baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getSetId() {
		return setId;
	}

	public void setSetId(Long setId) {
		this.setId = setId;
	}

	public String getAttrId() {
		return attrId;
	}

	public void setAttrId(String attrId) {
		this.attrId = attrId;
	}

	public Integer getAttrSortOrder() {
		return attrSortOrder;
	}

	public void setAttrSortOrder(Integer attrSortOrder) {
		this.attrSortOrder = attrSortOrder;
	}

	public String getAttrOrderType() {
		return attrOrderType;
	}

	public void setAttrOrderType(String attrOrderType) {
		this.attrOrderType = attrOrderType;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public OffsetDateTime getCrDate() {
		return crDate;
	}

	public void setCrDate(OffsetDateTime crDate) {
		this.crDate = crDate;
	}
}
