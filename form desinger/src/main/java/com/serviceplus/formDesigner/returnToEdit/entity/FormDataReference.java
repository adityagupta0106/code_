package com.serviceplus.formDesigner.returnToEdit.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "form_data_reference", schema = "schm_sp")
@IdClass(FormDataReference.FormDataReferenceId.class)
public class FormDataReference {

	@Id
	@Column(name = "application_id", nullable = false)
	private String applicationId;

	@Id
	@Column(name = "data_id", nullable = false)
	private String dataId;

	@Column(name = "base_service_id", nullable = false)
	private Integer baseServiceId;

	@Column(name = "service_id", nullable = false)
	private Integer serviceId;

	@Column(name = "task_id", nullable = false)
	private String taskId;

	@Column(name = "holder_id", nullable = false)
	private String holderId;

	@Column(name = "form_id", nullable = false)
	private String formId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@Column(name = "cr_date")
	private LocalDateTime crDate;

	@PrePersist
	protected void onCreate() {
	    if (crDate == null) {
	        crDate = LocalDateTime.now();
	    }
	}
	
	public FormDataReference() {
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public LocalDateTime getCrDate() {
		return crDate;
	}

	public void setCrDate(LocalDateTime crDate) {
		this.crDate = crDate;
	}

	public static class FormDataReferenceId implements Serializable {

		private static final long serialVersionUID = 1L;

		private String applicationId;
		private String dataId;

		public FormDataReferenceId() {
		}

		public FormDataReferenceId(String applicationId, String dataId) {
			this.applicationId = applicationId;
			this.dataId = dataId;
		}

		public String getApplicationId() {
			return applicationId;
		}

		public void setApplicationId(String applicationId) {
			this.applicationId = applicationId;
		}

		public String getDataId() {
			return dataId;
		}

		public void setDataId(String dataId) {
			this.dataId = dataId;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof FormDataReferenceId)) {
				return false;
			}

			FormDataReferenceId that = (FormDataReferenceId) obj;
			return Objects.equals(applicationId, that.applicationId) && Objects.equals(dataId, that.dataId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(applicationId, dataId);
		}
	}
}
