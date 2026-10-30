package com.serviceplus.tracking.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class TaskDetailResponse {

    private String taskId;
    private String taskName;
    private JsonNode taskProperty;

    public JsonNode getTaskProperty() {
        return taskProperty;
    }

    public void setTaskProperty(JsonNode taskProperty) {
        this.taskProperty = taskProperty;
    }
    private Long pendingCount;

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

    public Long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Long pendingCount) {
        this.pendingCount = pendingCount;
    }
}
