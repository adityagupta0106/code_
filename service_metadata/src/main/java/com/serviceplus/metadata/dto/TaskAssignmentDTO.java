package com.serviceplus.metadata.dto;

public class TaskAssignmentDTO {

    private String taskId;
    private String taskName;

    public TaskAssignmentDTO() {
    }

    public TaskAssignmentDTO(String taskId, String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
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
