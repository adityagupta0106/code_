package com.serviceplus.metadata.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ServiceParameterDetailsDTO {

	private Integer serviceId;
	private List<TemplateDetails> templateDetails;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<TaskDetails> taskDetails;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<SystemAttribute> systemAttributes;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<EnclosureDetails> enclosureDetails;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<PatternAttribute> patternAttributes;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public List<TemplateDetails> getTemplateDetails() {
		return templateDetails;
	}

	public void setTemplateDetails(List<TemplateDetails> templateDetails) {
		this.templateDetails = templateDetails;
	}

	public List<TaskDetails> getTaskDetails() {
		return taskDetails;
	}

	public void setTaskDetails(List<TaskDetails> taskDetails) {
		this.taskDetails = taskDetails;
	}

	public List<SystemAttribute> getSystemAttributes() {
		return systemAttributes;
	}

	public void setSystemAttributes(List<SystemAttribute> systemAttribute) {
		this.systemAttributes = systemAttribute;
	}

	public List<EnclosureDetails> getEnclosureDetails() {
		return enclosureDetails;
	}

	public void setEnclosureDetails(List<EnclosureDetails> enclosureDetails) {
		this.enclosureDetails = enclosureDetails;
	}

	public List<PatternAttribute> getPatternAttributes() {
		return patternAttributes;
	}

	public void setPatternAttributes(List<PatternAttribute> patternAttributes) {
		this.patternAttributes = patternAttributes;
	}

	public static class TemplateDetails {
		private String templateName;
		private String taskId;
		private String formId;
		private String holderId;
		private List<Attribute> attributeList;

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public List<Attribute> getAttributeList() {
			return attributeList;
		}

		public void setAttributeList(List<Attribute> attributeList) {
			this.attributeList = attributeList;
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

	}

	public static class Attribute {
		private String attributeId;
		private String attributeLabel;
		private Integer typeId;
		private String type;
		private Integer hierarchyLevel;
		private Boolean required;

		public Boolean getRequired() {
			return required;
		}

		public void setRequired(Boolean required) {
			this.required = required;
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

		public Integer getTypeId() {
			return typeId;
		}

		public void setTypeId(Integer typeId) {
			this.typeId = typeId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Integer getHierarchyLevel() {
			return hierarchyLevel;
		}

		public void setHierarchyLevel(Integer hierarchyLevel) {
			this.hierarchyLevel = hierarchyLevel;
		}

	}

	public static class TaskDetails {
		private String taskName;
		private String taskId;
		private String type;

		public String getTaskName() {
			return taskName;
		}

		public void setTaskName(String taskName) {
			this.taskName = taskName;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static class SystemAttribute {
		private String attributeId;
		private String attributeLabel;
		private String type;

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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static class EnclosureDetails {
		private String enclosureId;
		private String enclosureLabel;
		private String type;

		public String getEnclosureId() {
			return enclosureId;
		}

		public void setEnclosureId(String enclosureId) {
			this.enclosureId = enclosureId;
		}

		public String getEnclosureLabel() {
			return enclosureLabel;
		}

		public void setEnclosureLabel(String enclosureLabel) {
			this.enclosureLabel = enclosureLabel;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static class PatternAttribute {
		private String attributeId;
		private String attributeLabel;
		private String type;

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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}
}
