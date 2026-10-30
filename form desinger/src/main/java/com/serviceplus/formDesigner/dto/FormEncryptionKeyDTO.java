package com.serviceplus.formDesigner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FormEncryptionKeyDTO {
	@NotNull(message = "Service ID must not be null")
    private Integer serviceId;

    @NotBlank(message = "Form ID must not be blank")
    private String formId;

    @NotBlank(message = "Task ID must not be blank")
    private String taskId;

    @NotNull(message = "Default form encryption flag is required")
    private Boolean defaultFormEncryptionRequired;

    @NotNull(message = "Encrypt data in transit flag is required")
    private Boolean encryptDataInTransit;

    @NotNull(message = "Encrypt data at rest flag is required")
    private Boolean encryptDataAtRest;

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

	public Boolean getDefaultFormEncryptionRequired() {
		return defaultFormEncryptionRequired;
	}

	public void setDefaultFormEncryptionRequired(Boolean defaultFormEncryptionRequired) {
		this.defaultFormEncryptionRequired = defaultFormEncryptionRequired;
	}

	public Boolean getEncryptDataInTransit() {
		return encryptDataInTransit;
	}

	public void setEncryptDataInTransit(Boolean encryptDataInTransit) {
		this.encryptDataInTransit = encryptDataInTransit;
	}

	public Boolean getEncryptDataAtRest() {
		return encryptDataAtRest;
	}

	public void setEncryptDataAtRest(Boolean encryptDataAtRest) {
		this.encryptDataAtRest = encryptDataAtRest;
	}
    
}
