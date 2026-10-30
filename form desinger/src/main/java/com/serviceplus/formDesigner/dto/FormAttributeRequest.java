package com.serviceplus.formDesigner.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class FormAttributeRequest {

	@NotEmpty(message="Form Ids can not me empty")
	private List<String> formIds;

	public List<String> getFormIds() {
		return formIds;
	}

	public void setFormIds(List<String> formIds) {
		this.formIds = formIds;
	}

}