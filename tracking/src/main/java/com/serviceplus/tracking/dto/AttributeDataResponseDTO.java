package com.serviceplus.tracking.dto;

import java.util.Map;

public class AttributeDataResponseDTO {

	private String attrId;

	private Map<String, Object> attrData;

	public AttributeDataResponseDTO() {
	}

	public AttributeDataResponseDTO(String attrId, Map<String, Object> attrData) {

		this.attrId = attrId;
		this.attrData = attrData;
	}

	public String getAttrId() {
		return attrId;
	}

	public void setAttrId(String attrId) {
		this.attrId = attrId;
	}

	public Map<String, Object> getAttrData() {
		return attrData;
	}

	public void setAttrData(Map<String, Object> attrData) {
		this.attrData = attrData;
	}
}