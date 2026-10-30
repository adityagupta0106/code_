package com.serviceplus.tracking.entity;

import java.util.Date;

import com.serviceplus.tracking.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_filter_config", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class InboxSentBoxFilterConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "filter_id", nullable = false)
	private Long filterId;

	@Column(name = "base_service_id")
	private Long baseServiceId;

	@Column(name = "service_id", nullable = false)
	private Long serviceId;
	
	@Column(name = "task_id")
	private String taskId;

	@Column(name = "attr_label")
	private String attrLabel;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@Column(name = "attr_type", length = 1)
	private Character attrType;
	
	@Column(name = "filter_type", length = 1)
	private Character filterType;
	
	@Column(name = "attr_form_id")
	private String attrFormId;

	@Column(name = "attr_id")
	private String attrId;

	@Column(name = "attr_order")
	private Integer attrOrder;

	@Column(name = "filter_condition")
	private String filterCondition;

	@Column(name = "cr_date")
	private Date crDate;

	@Column(name = "up_date")
	private Date upDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAttrLabel() {
		return attrLabel;
	}

	public void setAttrLabel(String attrLabel) {
		this.attrLabel = attrLabel;
	}

	public Long getFilterId() {
		return filterId;
	}

	public Character getFilterType() {
		return filterType;
	}

	public void setFilterType(Character filterType) {
		this.filterType = filterType;
	}

	public void setFilterId(Long filterId) {
		this.filterId = filterId;
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

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Character getAttrType() {
		return attrType;
	}

	public void setAttrType(Character attrType) {
		this.attrType = attrType;
	}

	public String getAttrId() {
		return attrId;
	}

	public void setAttrId(String attrId) {
		this.attrId = attrId;
	}

	public Integer getAttrOrder() {
		return attrOrder;
	}

	public void setAttrOrder(Integer attrOrder) {
		this.attrOrder = attrOrder;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public Date getCrDate() {
		return crDate;
	}

	public void setCrDate(Date crDate) {
		this.crDate = crDate;
	}

	public Date getUpDate() {
		return upDate;
	}

	public void setUpDate(Date upDate) {
		this.upDate = upDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAttrFormId() {
		return attrFormId;
	}

	public void setAttrFormId(String attrFormId) {
		this.attrFormId = attrFormId;
	}
	
}