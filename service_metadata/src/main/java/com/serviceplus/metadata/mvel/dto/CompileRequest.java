package com.serviceplus.metadata.mvel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@NotNull(message = "Object cannot be null")
public class CompileRequest {
	@NotBlank(message = "Expression body (fnBody) must not be blank")
	private String fnBody;

	public String getFnBody() {
		return fnBody;
	}

	public void setFnBody(String fnBody) {
		this.fnBody = fnBody;
	}

}
