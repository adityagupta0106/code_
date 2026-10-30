package com.serviceplus.metadata.dto;

public class NotificationDetails {
	private Long notificationId;
	private String taskId;
	private String triggerPointId;
	public Long getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTriggerPointId() {
		return triggerPointId;
	}
	public void setTriggerPointId(String triggerPointId) {
		this.triggerPointId = triggerPointId;
	}
	
}
