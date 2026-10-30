package com.serviceplus.metadata.dto;

import java.util.List;

public class TaskPropertyDetailsEvent {

    private String eventType;
    private String tenantId;

    private Integer serviceId;
    private Integer baseServiceId;

    private List<TaskPropertyDetails> tasks;

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public List<TaskPropertyDetails> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskPropertyDetails> tasks) {
		this.tasks = tasks;
	}
    
}