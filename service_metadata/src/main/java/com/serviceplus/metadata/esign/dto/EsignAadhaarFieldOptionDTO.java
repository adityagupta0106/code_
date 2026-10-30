package com.serviceplus.metadata.esign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.dto.LabelValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsignAadhaarFieldOptionDTO {

    private LabelValue task;
    private LabelValue attribute;

    public EsignAadhaarFieldOptionDTO() {
    }

	public LabelValue getTask() {
		return task;
	}

	public void setTask(LabelValue task) {
		this.task = task;
	}

	public LabelValue getAttribute() {
		return attribute;
	}

	public void setAttribute(LabelValue attribute) {
		this.attribute = attribute;
	}

    
}
