package com.serviceplus.metadata.dto;

public class TaskAssignmentHierarchyDTO {

	private String taskId;
	private String taskName;
	private Integer levelCode;
	private String levelSpecificName;

	public TaskAssignmentHierarchyDTO() {
	}

	public TaskAssignmentHierarchyDTO(String taskId, String taskName, Integer levelCode, String levelSpecificName) {
		this.taskId = taskId;
		this.taskName = taskName;
		this.levelCode = levelCode;
		this.levelSpecificName = levelSpecificName;
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

	public Integer getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(Integer levelCode) {
		this.levelCode = levelCode;
	}

	public String getLevelSpecificName() {
		return levelSpecificName;
	}

	public void setLevelSpecificName(String levelSpecificName) {
		this.levelSpecificName = levelSpecificName;
	}
}