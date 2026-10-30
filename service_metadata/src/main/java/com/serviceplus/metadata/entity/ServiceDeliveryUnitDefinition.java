package com.serviceplus.metadata.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.dto.ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_delivery_unit_definition", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ServiceDeliveryUnitDefinition {

	@Id
	@Column(name = "service_id", nullable = false)
	private Integer serviceId;

	@Column(name = "delivery_unit_json", nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<ServiceDeliveryUnitJSON> serviceDeliveryUnits;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "state_id", nullable = false)
	private Integer stateId;

	@Column(name = "version_no", nullable = false)
	private Integer versionNo;

	@Column(name = "minor_version_no", nullable = false)
	private Integer minorVersionNo;

	@Column(name = "c_date", nullable = false)
	private Date creationDate;

	@Column(name = "u_date")
	private Date updateDate;
	
	@Column(name= "source")
	private String source;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public List<ServiceDeliveryUnitJSON> getServiceDeliveryUnits() {
		return serviceDeliveryUnits;
	}

	public void setServiceDeliveryUnits(List<ServiceDeliveryUnitJSON> serviceDeliveryUnits) {
		this.serviceDeliveryUnits = serviceDeliveryUnits;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Integer getMinorVersionNo() {
		return minorVersionNo;
	}

	public void setMinorVersionNo(Integer minorVersionNo) {
		this.minorVersionNo = minorVersionNo;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
