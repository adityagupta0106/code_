package com.serviceplus.formDesigner.dto;

import java.time.LocalDate;
import java.util.List;

public class FormProcessCacheDTO {
	private List<Attribute> attributes;
	private String formId;

	public static class Attribute {

		private String attributeId;
		private String attributeLabel;
		private String parentId;
		private Integer typeId;
		private String type;
		private Boolean required;
		private Integer min;
		private Integer max;
		private Integer minLenght;
		private Integer maxLength;
		private String errorLabel;
		private LocalDate maxDate;
		private LocalDate minDate;
		private Boolean isEncryptionDataAtRest;
		private List<LabelValue> optionList;

		public Attribute(String attributeId,String parentId, String attributeLabel, Integer typeId, String type, Boolean required, Integer min, Integer max, Integer minLenght,
				Integer maxLength, String errorLabel, LocalDate maxDate, LocalDate minDate,List<LabelValue> optionList,Boolean isEncryptionDataAtRest) {
			super();
			this.attributeId = attributeId;
			this.parentId = parentId;
			this.attributeLabel = attributeLabel;
			this.typeId = typeId;
			this.type = type;
			this.required = required;
			this.min= min;
			this.max = max;
			this.minLenght = minLenght;
			this.maxLength = maxLength;
			this.errorLabel = errorLabel;
			this.maxDate = maxDate;
			this.minDate = minDate;
			this.optionList = optionList;
			this.isEncryptionDataAtRest = isEncryptionDataAtRest;
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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Boolean getRequired() {
			return required;
		}

		public void setRequired(Boolean required) {
			this.required = required;
		}

		public Integer getMin() {
			return min;
		}

		public void setMin(Integer min) {
			this.min = min;
		}

		public Integer getMax() {
			return max;
		}

		public void setMax(Integer max) {
			this.max = max;
		}

		public Integer getMinLenght() {
			return minLenght;
		}

		public void setMinLenght(Integer minLenght) {
			this.minLenght = minLenght;
		}

		public Integer getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(Integer maxLength) {
			this.maxLength = maxLength;
		}

		public String getErrorLabel() {
			return errorLabel;
		}

		public void setErrorLabel(String errorLabel) {
			this.errorLabel = errorLabel;
		}

		public LocalDate getMaxDate() {
			return maxDate;
		}

		public void setMaxDate(LocalDate maxDate) {
			this.maxDate = maxDate;
		}

		public LocalDate getMinDate() {
			return minDate;
		}

		public void setMinDate(LocalDate minDate) {
			this.minDate = minDate;
		}

		public List<LabelValue> getOptionList() {
			return optionList;
		}

		public void setOptionList(List<LabelValue> optionList) {
			this.optionList = optionList;
		}

		public Boolean getIsEncryptionDataAtRest() {
			return isEncryptionDataAtRest;
		}

		public void setIsEncryptionDataAtRest(Boolean isEncryptionDataAtRest) {
			this.isEncryptionDataAtRest = isEncryptionDataAtRest;
		}
		
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	public static class LabelValue {
		private String label;
		private String value;

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
