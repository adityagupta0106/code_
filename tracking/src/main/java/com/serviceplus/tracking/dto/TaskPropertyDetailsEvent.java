package com.serviceplus.tracking.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class TaskPropertyDetailsEvent {

    private String eventType;
    private String tenantId;

    private Integer serviceId;
    private Integer baseServiceId;

    private List<TaskPropertyDetails> tasks;

    public static class TaskPropertyDetails {

        private String taskId;
        private String taskName;
        private Integer taskType;

        private JsonNode taskProperty;

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

		public Integer getTaskType() {
			return taskType;
		}

		public void setTaskType(Integer taskType) {
			this.taskType = taskType;
		}

		public JsonNode getTaskProperty() {
			return taskProperty;
		}

		public void setTaskProperty(JsonNode taskProperty) {
			this.taskProperty = taskProperty;
		}
    }

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