package com.serviceplus.metadata.inboxSentboxFilterConfig.dto;

import java.util.List;

import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.inboxSentboxFilterConfig.enums.Condition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InboxSentBoxFilterDTO {

	private Long id;

	@NotNull(message = "service can't be null")
	@Valid
	private LabelValue service;
	
	@NotBlank(message = "name can't be blank")
	private String name;

	@NotNull(message = "filterType can't be null")
	@Valid
	private LabelValue filterType;

	@NotNull(message = "targetTask can't be null")
	@Valid
	private List<LabelValue> targetTask;

	private List<FilterAttributeDTO> inputFilters;

	private List<FilterAttributeDTO> outputFilters;
	
	private Condition condition;

	public static class FilterAttributeDTO {

		@NotNull(message = "isSystemVar can't be null")
		private Boolean isSystemVar;

		@NotBlank(message = "id can't be blank")
		private String id;
		
		private String attrLabel;
		
		private String formId;
		private String holderId;

		@NotNull(message = "sortOrder can't be null")
		@Min(value = 1, message = "sortOrder must be greater than 0")
		private Integer sortOrder;

		public String getAttrLabel() {
			return attrLabel;
		}

		public void setAttrLabel(String attrLabel) {
			this.attrLabel = attrLabel;
		}

		public Boolean getIsSystemVar() {
			return isSystemVar;
		}

		public void setIsSystemVar(Boolean isSystemVar) {
			this.isSystemVar = isSystemVar;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
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

		public Integer getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(Integer sortOrder) {
			this.sortOrder = sortOrder;
		}
	}

	public List<LabelValue> getTargetTask() {
		return targetTask;
	}

	public void setTargetTask(List<LabelValue> targetTask) {
		this.targetTask = targetTask;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LabelValue getService() {
		return service;
	}

	public void setService(LabelValue service) {
		this.service = service;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LabelValue getFilterType() {
		return filterType;
	}

	public void setFilterType(LabelValue filterType) {
		this.filterType = filterType;
	}

	public List<FilterAttributeDTO> getInputFilters() {
		return inputFilters;
	}

	public void setInputFilters(List<FilterAttributeDTO> inputFilters) {
		this.inputFilters = inputFilters;
	}

	public List<FilterAttributeDTO> getOutputFilters() {
		return outputFilters;
	}

	public void setOutputFilters(List<FilterAttributeDTO> outputFilters) {
		this.outputFilters = outputFilters;
	}

}
