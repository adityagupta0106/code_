package com.serviceplus.metadata.pattern.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "pattern_configuration", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class PatternConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pattern_name", nullable = false)
	private String patternName;

	@Column(name = "config_json", columnDefinition = "jsonb", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private PatternTemplateDTO configJson;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@Column(name = "entity_id")
	private Integer entityId;

	@Column(name = "cr_by", nullable = false)
	private Long createdBy;

	@Column(name = "cr_date", nullable = false)
	private LocalDateTime createdDate;

	@Column(name = "up_date")
	private LocalDateTime updatedDate;

	@Column(name = "up_by")
	private Long updatedBy;

	public PatternConfiguration() {
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

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public PatternTemplateDTO getConfigJson() {
	    return configJson;
	}

	public void setConfigJson(PatternTemplateDTO configJson) {
	    this.configJson = configJson;
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
