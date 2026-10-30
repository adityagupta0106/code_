package com.serviceplus.tracking.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class TaskDropdownDTO {

    private String taskId;
    private String taskName;
    private JsonNode taskProperty;

    public JsonNode getTaskProperty() {
        return taskProperty;
    }

    public void setTaskProperty(JsonNode taskProperty) {
        this.taskProperty = taskProperty;
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
}
