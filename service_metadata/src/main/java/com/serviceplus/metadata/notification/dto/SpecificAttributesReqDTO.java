package com.serviceplus.metadata.notification.dto;

import java.util.Set;

public class SpecificAttributesReqDTO {

    private Set<String> formIds;
    private String attrType;

	public Set<String> getFormIds() {
		return formIds;
	}

	public void setFormIds(Set<String> formIds) {
		this.formIds = formIds;
	}

	public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }
}