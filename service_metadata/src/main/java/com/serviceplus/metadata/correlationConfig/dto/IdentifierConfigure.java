package com.serviceplus.metadata.correlationConfig.dto;

import java.util.List;

import com.serviceplus.metadata.dto.LabelValue;

import jakarta.validation.constraints.NotNull;

public class IdentifierConfigure {

	@NotNull(message = "tasks can't be null")
	private List<LabelValue> tasks;

	@NotNull(message = "inputAttributes can't be null")
	private List<CorrelationFieldDTO> inputAttributes;

	@NotNull(message = "outputAttributes can't be null")
	private List<OutputAttributes> outputAttributes;

	@NotNull(message = "sortingAttributes can't be null")
	private List<CorrelationFieldDTO> sortingAttributes;

	public static class OutputAttributes {
		private String name;
		private String outputSetid;
		private List<CorrelationFieldDTO> attributes;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getOutputSetid() {
			return outputSetid;
		}

		public void setOutputSetid(String outputSetid) {
			this.outputSetid = outputSetid;
		}

		public List<CorrelationFieldDTO> getAttributes() {
			return attributes;
		}

		public void setAttributes(List<CorrelationFieldDTO> attributes) {
			this.attributes = attributes;
		}

	}

	public List<LabelValue> getTasks() {
		return tasks;
	}

	public void setTasks(List<LabelValue> tasks) {
		this.tasks = tasks;
	}

	public List<CorrelationFieldDTO> getInputAttributes() {
		return inputAttributes;
	}

	public void setInputAttributes(List<CorrelationFieldDTO> inputAttributes) {
		this.inputAttributes = inputAttributes;
	}

	public List<OutputAttributes> getOutputAttributes() {
		return outputAttributes;
	}

	public void setOutputAttributes(List<OutputAttributes> outputAttributes) {
		this.outputAttributes = outputAttributes;
	}

	public List<CorrelationFieldDTO> getSortingAttributes() {
		return sortingAttributes;
	}

	public void setSortingAttributes(List<CorrelationFieldDTO> sortingAttributes) {
		this.sortingAttributes = sortingAttributes;
	}


}
