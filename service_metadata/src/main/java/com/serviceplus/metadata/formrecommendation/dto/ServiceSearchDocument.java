package com.serviceplus.metadata.formrecommendation.dto;

import java.util.List;

public class ServiceSearchDocument {
	private Integer serviceId;
	private String serviceName;
	private String serviceDescription;
	private String tenantId;
	private String departmentId;
	private String locationId;

	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public List<AttachedFormDTO> getAttachedForm() {
		return attachedForm;
	}

	public void setAttachedForm(List<AttachedFormDTO> attachedForm) {
		this.attachedForm = attachedForm;
	}

	private List<AttachedFormDTO> attachedForm;

	public static class AttachedFormDTO {

		private String taskName;
		private String formId;
		private String formName;

		public String getTaskName() {
			return taskName;
		}

		public void setTaskName(String taskName) {
			this.taskName = taskName;
		}

		public String getFormId() {
			return formId;
		}

		public void setFormId(String formId) {
			this.formId = formId;
		}

		public String getFormName() {
			return formName;
		}

		public void setFormName(String formName) {
			this.formName = formName;
		}

	}

}
