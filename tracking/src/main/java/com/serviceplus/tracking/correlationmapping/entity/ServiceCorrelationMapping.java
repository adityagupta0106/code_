package com.serviceplus.tracking.correlationmapping.entity;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

import java.util.Date;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_correlation_mapping", schema = SP_SCHEMA_NAME)
public class ServiceCorrelationMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "base_service_id")
	private Long baseServiceId;

	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "task_id")
	private String taskId;

	@Column(name = "combination_id")
	private Long combinationId;

	@Column(name = "mapped_base_service_id")
	private Long mappedBaseServiceId;

	@Column(name = "mapped_service_id")
	private Long mappedServiceId;

	@Column(name = "mapped_set_id")
	private Long mappedSetId;

	@Column(name = "output_set_id")
	private String outputSetId;

	@Column(name = "source_type")
	private String sourceType;

	@Column(name = "priority")
	private Integer priority;

	@Column(name = "search_next")
	private Boolean searchNext;

	@Column(name = "appl_status")
	private Integer applStatus;

	@Column(name = "mappped_completed_task_id")
	private String mapppedCompletedTaskId;

	@Column(name = "tenant_id")
	private String tenantId;

	@Column(name = "entity_id")
	private Integer entityId;

	@Column(name = "cr_date")
	private Date crDate;

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

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Long getCombinationId() {
		return combinationId;
	}

	public void setCombinationId(Long combinationId) {
		this.combinationId = combinationId;
	}

	public Long getMappedBaseServiceId() {
		return mappedBaseServiceId;
	}

	public void setMappedBaseServiceId(Long mappedBaseServiceId) {
		this.mappedBaseServiceId = mappedBaseServiceId;
	}

	public Long getMappedServiceId() {
		return mappedServiceId;
	}

	public void setMappedServiceId(Long mappedServiceId) {
		this.mappedServiceId = mappedServiceId;
	}

	public Long getMappedSetId() {
		return mappedSetId;
	}

	public void setMappedSetId(Long mappedSetId) {
		this.mappedSetId = mappedSetId;
	}

	public String getOutputSetId() {
		return outputSetId;
	}

	public void setOutputSetId(String outputSetId) {
		this.outputSetId = outputSetId;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Boolean getSearchNext() {
		return searchNext;
	}

	public void setSearchNext(Boolean searchNext) {
		this.searchNext = searchNext;
	}

	public Integer getApplStatus() {
		return applStatus;
	}

	public void setApplStatus(Integer applStatus) {
		this.applStatus = applStatus;
	}

	public String getMapppedCompletedTaskId() {
		return mapppedCompletedTaskId;
	}

	public void setMapppedCompletedTaskId(String mapppedCompletedTaskId) {
		this.mapppedCompletedTaskId = mapppedCompletedTaskId;
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

	public Date getCrDate() {
		return crDate;
	}

	public void setCrDate(Date crDate) {
		this.crDate = crDate;
	}
}