package com.serviceplus.metadata.dto;

import java.util.List;
import java.util.Set;

public class TaskDTO {

	private Integer serviceId;
	private List<Task> taskList;
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public List<Task> getTaskList() {
		return taskList;
	}
	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}
	
	public static class Task{
		private String taskId;
		private String taskName;
		private String taskType;
		private List<TaskReference> nextTasks;
		private List<TaskReference> prevTasks;
		public static class TaskReference {
		    private String taskId;
		    private String taskName;

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
		public String getTaskType() {
			return taskType;
		}
		public void setTaskType(String taskType) {
			this.taskType = taskType;
		}
		public List<TaskReference> getNextTasks() {
			return nextTasks;
		}
		public void setNextTasks(List<TaskReference> nextTasks) {
			this.nextTasks = nextTasks;
		}
		public List<TaskReference> getPrevTasks() {
			return prevTasks;
		}
		public void setPrevTasks(List<TaskReference> prevTasks) {
			this.prevTasks = prevTasks;
		}
		
	}
}
