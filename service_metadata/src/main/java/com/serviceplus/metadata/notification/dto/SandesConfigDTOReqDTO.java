package com.serviceplus.metadata.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SandesConfigDTOReqDTO {
	private Long id;

	@NotNull(message = "Data can't be null.")
	private SandesConfigDTO data;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SandesConfigDTO getData() {
		return data;
	}

	public void setData(SandesConfigDTO data) {
		this.data = data;
	}
	

}
