package com.serviceplus.formDesigner.autosave.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationAutoSaveDTO {
	@NotNull(message = "Service ID must not be null")
	private Integer serviceId;

	@NotBlank(message = "Form ID must not be blank")
	private String formId;

	@NotBlank(message = "Task ID must not be blank")
	private String taskId;

	private String applicationId;

	@NotNull(message = "Form data must not be null")
	private Object formData;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public Object getFormData() {
		return formData;
	}

	public void setFormData(Object formData) {
		this.formData = formData;
	}

}
