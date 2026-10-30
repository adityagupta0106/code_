package com.serviceplus.metadata.notification.entity;

import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.notification.dto.ConfigCommonDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_provider_config", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class NotificationProviderConfig {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "properties_json", columnDefinition = "jsonb", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private ConfigCommonDTO configDTO;

	@Column(name = "c_user_id", nullable = false)
	private Long userId;

	@Column(name = "c_date", nullable = false)
	private Date cDate;

	@Column(name = "u_date")
	private Date uDate;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@Column(name = "clc_id")
	private Integer stateId;

	@Column(name = "entity_id", nullable = false)
	private Integer departmentId;

	@Column(name = "location_id")
	private Integer locationId;

	@Column(name = "status", nullable = false, length = 1)
	private String status;

	@Column(name = "remark")
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ConfigCommonDTO getConfigDTO() {
		return configDTO;
	}

	public void setConfigDTO(ConfigCommonDTO configDTO) {
		this.configDTO = configDTO;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getcDate() {
		return cDate;
	}

	public void setcDate(Date cDate) {
		this.cDate = cDate;
	}

	public Date getuDate() {
		return uDate;
	}

	public void setuDate(Date uDate) {
		this.uDate = uDate;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}