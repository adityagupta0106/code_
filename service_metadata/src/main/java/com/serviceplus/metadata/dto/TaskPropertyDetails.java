package com.serviceplus.metadata.dto;

import com.google.gson.JsonObject;

public class TaskPropertyDetails {

    private String taskId;
    private String taskName;
    private Integer taskType;

    private JsonObject taskProperty;

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

	public JsonObject getTaskProperty() {
		return taskProperty;
	}

	public void setTaskProperty(JsonObject taskProperty) {
		this.taskProperty = taskProperty;
	}
}
