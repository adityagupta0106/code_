package com.serviceplus.metadata.notification.dto;

import java.util.List;

public class ProcessFlowFormAttrResDTO {
	private String taskId;
	private String taskName;
	private String formId;
	private String holderId;
	private String templateName;
	private List<TaskAttributeDTO> attributes;

	public class TaskAttributeDTO {

		private String attributeId;
		private String attributeLabel;

		public String getAttributeId() {
			return attributeId;
		}

		public void setAttributeId(String attributeId) {
			this.attributeId = attributeId;
		}

		public String getAttributeLabel() {
			return attributeLabel;
		}

		public void setAttributeLabel(String attributeLabel) {
			this.attributeLabel = attributeLabel;
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

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<TaskAttributeDTO> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<TaskAttributeDTO> attributes) {
		this.attributes = attributes;
	}

}
