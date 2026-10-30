package com.serviceplus.metadata.formrecommendation.dto;

public class UniqueFormResponseDTO {

	private String formId;
	private String formName;

	public UniqueFormResponseDTO() {
		super();
	}

	public UniqueFormResponseDTO(String formId, String formName) {
		super();
		this.formId = formId;
		this.formName = formName;
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
}
