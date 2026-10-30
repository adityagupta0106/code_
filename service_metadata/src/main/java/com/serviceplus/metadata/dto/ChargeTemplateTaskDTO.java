package com.serviceplus.metadata.dto;

public class ChargeTemplateTaskDTO {
    private String taskId;
    private String taskName;
    private Boolean mapped;
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
	public Boolean getMapped() {
		return mapped;
	}
	public void setMapped(Boolean mapped) {
		this.mapped = mapped;
	}
	public ChargeTemplateTaskDTO(String taskId, String taskName, Boolean mapped) {
		super();
		this.taskId = taskId;
		this.taskName = taskName;
		this.mapped = mapped;
	}
	public ChargeTemplateTaskDTO() {
		super();
	}
    
}
