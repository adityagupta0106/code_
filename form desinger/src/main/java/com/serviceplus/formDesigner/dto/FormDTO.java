package com.serviceplus.formDesigner.dto;

import java.util.List;

public class FormDTO {
	private List<Attribute> attributes;
	private String formName;
	public static class Attribute {
		private String attributeId;
		private String parentId;
		private String attributeLabel;
		private Integer typeId;
		private Boolean required;

		public Attribute(String attributeId,String parentId, String attributeLabel,Integer typeId, Boolean required) {
			this.attributeId = attributeId;
			this.parentId = parentId;
			this.attributeLabel = attributeLabel;
			this.typeId = typeId;
			this.required = required;
		}
		public String getAttributeId() {
			return attributeId;
		}

		public void setAttributeId(String attributeId) {
			this.attributeId = attributeId;
		}

		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
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
		public Boolean getRequired() {
			return required;
		}
		public void setRequired(Boolean required) {
			this.required = required;
		}

	}

	public FormDTO(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

}