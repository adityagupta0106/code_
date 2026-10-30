package com.serviceplus.metadata.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ConfigurationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String tenantId;

	private Map<String, Object> data;

	private Date createdAt;

	private Date updatedAt;

	public ConfigurationDTO() {
	}

	public ConfigurationDTO(String tenantId, Map<String, Object> data, Date createdAt, Date updatedAt) {
		super();
		this.tenantId = tenantId;
		this.data = data;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}
