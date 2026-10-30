package com.serviceplus.formDesigner.request.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FormPublishLog {

	@Id
	private String holderId;

	private List<Log> logs;

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public static class Log {
		private String formId;
		private Integer version;
		private Long userId;
		private String tenantId;
		private Integer stateId;
		private Integer departmentId;
		private Integer locationId;
		private Date createdOn;

		public String getFormId() {
			return formId;
		}

		public void setFormId(String formId) {
			this.formId = formId;
		}

		public Integer getVersion() {
			return version;
		}

		public void setVersion(Integer version) {
			this.version = version;
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

		public Date getCreatedOn() {
			return createdOn;
		}

		public void setCreatedOn(Date createdOn) {
			this.createdOn = createdOn;
		}

		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}

	}
}
