package com.serviceplus.metadata.notification.dto;

import java.util.List;

public class SpecificAttributesDTO {

	private List<Attribute> attributes;

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public static class Attribute {
		private String attributeId;
		private String attributeLabel;
		private Integer typeId;
		private Integer hierachyLevel;

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

		public Integer getHierachyLevel() {
			return hierachyLevel;
		}

		public void setHierachyLevel(Integer hierachyLevel) {
			this.hierachyLevel = hierachyLevel;
		}
		

	}

}