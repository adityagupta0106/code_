package com.serviceplus.metadata.mvel.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_mvel_event_mapping", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class MvelEventMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id", nullable = false)
	private Long eventId;

	@Column(name = "service_id", nullable = false)
	private Integer serviceId;

	@Column(name = "version_no", nullable = false)
	private Integer versionNo;
	
	@Column(name = "mvel_event_mapped_json", nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private MvelEventMappingDTO mvelEventMappingDTO;

	@Column(name = "created_on", nullable = false, updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "created_by", nullable = false, updatable = false)
	private Long createdBy;

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public MvelEventMappingDTO getMvelEventMappingDTO() {
		return mvelEventMappingDTO;
	}

	public void setMvelEventMappingDTO(MvelEventMappingDTO mvelEventMappingDTO) {
		this.mvelEventMappingDTO = mvelEventMappingDTO;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

}