package com.serviceplus.metadata.correlationConfig.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.correlationConfig.enums.AttributeSource;
import com.serviceplus.metadata.correlationConfig.enums.SortingSequence;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CorrelationFieldDTO {

	@NotBlank(message = "attributeId can't be blank")
	private String attributeId;

	@NotBlank(message = "attributeLabel can't be blank")
	private String attributeLabel;

	@NotNull(message = "attributeSource can't be null")
	private AttributeSource attributeSource;

	private String formId;
	private String holderId;
	private String taskId;
	private String taskName;
	private Integer sortOrder;
	private SortingSequence sortSequence;
	

	public SortingSequence getSortSequence() {
		return sortSequence;
	}

	public void setSortSequence(SortingSequence sortSequence) {
		this.sortSequence = sortSequence;
	}

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

	public AttributeSource getAttributeSource() {
		return attributeSource;
	}

	public void setAttributeSource(AttributeSource attributeSource) {
		this.attributeSource = attributeSource;
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

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

}
