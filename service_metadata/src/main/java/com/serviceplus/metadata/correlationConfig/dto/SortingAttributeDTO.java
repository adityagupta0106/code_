package com.serviceplus.metadata.correlationConfig.dto;

public class SortingAttributeDTO {
	private String attrId;
	private Integer sortOrder;
	private String sortingSequence;

	public String getAttrId() {
		return attrId;
	}

	public void setAttrId(String attrId) {
		this.attrId = attrId;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortingSequence() {
		return sortingSequence;
	}

	public void setSortingSequence(String sortingSequence) {
		this.sortingSequence = sortingSequence;
	}

}
