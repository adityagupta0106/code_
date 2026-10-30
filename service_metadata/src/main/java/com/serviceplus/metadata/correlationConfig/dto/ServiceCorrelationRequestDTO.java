package com.serviceplus.metadata.correlationConfig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceCorrelationRequestDTO {

	private Integer setId;

	@NotNull(message = "serviceId can't be null")
	private Integer serviceId;

	@NotBlank(message = "setName can't be blank")
	private String setName;

	private String securityKey;
	
	@NotNull(message = "identifierConfigure can't be null")
	private IdentifierConfigure identifierConfigure;

	public Integer getSetId() {
		return setId;
	}

	public void setSetId(Integer setId) {
		this.setId = setId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public IdentifierConfigure getIdentifierConfigure() {
		return identifierConfigure;
	}

	public void setIdentifierConfigure(IdentifierConfigure identifierConfigure) {
		this.identifierConfigure = identifierConfigure;
	}


}
