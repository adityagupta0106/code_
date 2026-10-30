package com.serviceplus.metadata.correlationmapping.entity;

import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.correlationmapping.dto.ExternalDataMappingRequestDTO;
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
@Table(name = "external_data_mapping", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ExternalDataMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	
	@Column(name = "mapping_data_json", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ExternalDataMappingRequestDTO dto;
	
	@Column(name = "tenant_id", nullable = false)
	private String tenantId;
	
	@Column(name = "entity_id")
	private Integer entityId;

	@Column(name = "cr_by", nullable = false)
	private Long crBy;

	@Column(name = "cr_date", nullable = false)
	private Date crDate;

	@Column(name = "up_date")
	private Date upDate;

	@Column(name = "up_by")
	private Long upBy;

	@PrePersist
	protected void onCreate() {
		Date now = new Date();
		crDate = now;
		upDate = now;
	}

	@PreUpdate
	protected void onUpdate() {
		upDate = new Date();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ExternalDataMappingRequestDTO getDto() {
		return dto;
	}

	public void setDto(ExternalDataMappingRequestDTO dto) {
		this.dto = dto;
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

	public Long getCrBy() {
		return crBy;
	}

	public void setCrBy(Long crBy) {
		this.crBy = crBy;
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

	public Long getUpBy() {
		return upBy;
	}

	public void setUpBy(Long upBy) {
		this.upBy = upBy;
	}

}
