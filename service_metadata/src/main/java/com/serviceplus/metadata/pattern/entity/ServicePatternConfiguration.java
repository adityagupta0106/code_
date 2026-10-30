package com.serviceplus.metadata.pattern.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.pattern.dto.ServicePatternConfigurationDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "service_pattern_configuration", schema = ApplicationConstants.SP_SCHEMA_NAME, uniqueConstraints = {
		@UniqueConstraint(name = "service_pattern_configuration_service_pattern_uk", columnNames = { "service_id",
				"pattern_id" }) })
public class ServicePatternConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pattern_id", nullable = false)
	private Long patternId;

	@Column(name = "service_id", nullable = false)
	private Long serviceId;

	@Column(name = "base_service_id", nullable = false)
	private Long baseServiceId;

	@Column(name = "config_json", columnDefinition = "json", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private ServicePatternConfigurationDTO configJson;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@Column(name = "entity_id")
	private Long entityId;

	@Column(name = "cr_by", nullable = false)
	private Long createdBy;

	@Column(name = "cr_date", nullable = false)
	private LocalDateTime createdDate;

	@Column(name = "up_date")
	private LocalDateTime updatedDate;

	@Column(name = "up_by")
	private Long updatedBy;

	public ServicePatternConfiguration() {
		this.createdDate = LocalDateTime.now();
	}

	@PrePersist
	protected void onCreate() {
		if (this.createdDate == null) {
			this.createdDate = LocalDateTime.now();
		}
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedDate = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPatternId() {
		return patternId;
	}

	public void setPatternId(Long patternId) {
		this.patternId = patternId;
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

	public ServicePatternConfigurationDTO getConfigJson() {
		return configJson;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setConfigJson(ServicePatternConfigurationDTO configJson) {
		this.configJson = configJson;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}
}
