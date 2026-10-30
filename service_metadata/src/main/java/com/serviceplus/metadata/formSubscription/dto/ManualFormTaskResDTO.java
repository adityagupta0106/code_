package com.serviceplus.metadata.formSubscription.dto;

public class ManualFormTaskResDTO {

	private Integer serviceId;
	private String serviceName;
	private String taskId;
	private String taskName;
	private String formId;
	private String publishFormId;
	private Integer version;
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
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
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
	public String getPublishFormId() {
		return publishFormId;
	}
	public void setPublishFormId(String publishFormId) {
		this.publishFormId = publishFormId;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
}
