package com.serviceplus.metadata.inboxSentboxFilterConfig.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.inboxSentboxFilterConfig.dto.FilterJSONDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_filter", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class InboxSentBoxFilter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "base_service_id", nullable = false)
	private Long baseServiceId;
	
	@Column(name = "service_id", nullable = false)
	private Long serviceId;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;
	
	@Column(name = "filter_name", nullable = false)
	private String filterName;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "tasks_json", columnDefinition = "jsonb")
	private List<LabelValue> tasks;
	
	@Column(name = "filter_type", nullable = false, length = 1)
	private Character filterType;

	@Column(name = "filter_condition")
	private String condition="AND";
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "filter_json", columnDefinition = "jsonb")
	private FilterJSONDTO filterJSON;
	
	@Column(name = "filter_status", nullable = false)
	private Integer status;

	@Column(name = "cr_date", nullable = false)
	private Date crDate;

	@Column(name = "up_date", nullable = false)
	private Date upDate;

	@Column(name = "cr_by")
	private Long userId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Long baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public List<LabelValue> getTasks() {
		return tasks;
	}

	public void setTasks(List<LabelValue> tasks) {
		this.tasks = tasks;
	}

	public Character getFilterType() {
		return filterType;
	}

	public void setFilterType(Character filterType) {
		this.filterType = filterType;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public FilterJSONDTO getFilterJSON() {
		return filterJSON;
	}

	public void setFilterJSON(FilterJSONDTO filterJSON) {
		this.filterJSON = filterJSON;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
