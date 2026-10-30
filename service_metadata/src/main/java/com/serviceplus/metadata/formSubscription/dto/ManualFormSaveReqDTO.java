package com.serviceplus.metadata.formSubscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ManualFormSaveReqDTO {

	@NotNull(message = "serviceId cannot be null")
	private Integer serviceId;
	
	@NotBlank(message = "taskId cannot be blank")
	private String taskId;
	
	@NotBlank(message = "formId cannot be blank")
	private String formId;
	
	@NotBlank(message = "applyFormId cannot be blank")
	private String applyFormId;
	
	@NotNull(message = "accept cannot be null")
	private Boolean accept;

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

	public String getApplyFormId() {
		return applyFormId;
	}

	public void setApplyFormId(String applyFormId) {
		this.applyFormId = applyFormId;
	}

	public Boolean getAccept() {
		return accept;
	}

	public void setAccept(Boolean accept) {
		this.accept = accept;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

}
