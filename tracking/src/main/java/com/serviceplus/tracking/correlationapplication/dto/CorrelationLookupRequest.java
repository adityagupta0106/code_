package com.serviceplus.tracking.correlationapplication.dto;

import java.util.Map;

public class CorrelationLookupRequest {

	private Long serviceId;
	private String taskId;
	private Map<String, Object> formData;

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Map<String, Object> getFormData() {
		return formData;
	}

	public void setFormData(Map<String, Object> formData) {
		this.formData = formData;
	}
}