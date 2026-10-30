package com.serviceplus.metadata.formrecommendation.dto;

public class FormTaskDTO {
	private String formId;
	private String holderId;
	private String formName;
	private String taskName;

	public FormTaskDTO() {
		super();
	}

	public FormTaskDTO(String formId, String formName, String taskName) {
		super();
		this.formId = formId;
		this.formName = formName;
		this.taskName = taskName;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
