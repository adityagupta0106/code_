package com.serviceplus.metadata.dto;

import java.util.List;

public class Enclosure {
	private int fileSize;
	private LabelValue fileSizeUnit;
	private List<Object> mandatory;// need to check
	private LabelValue enclosureType;
	private List<LabelValue> allowedType;
	private String sortOrder;
	private List<LabelValue> documentsRecommended;

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public LabelValue getFileSizeUnit() {
		return fileSizeUnit;
	}

	public void setFileSizeUnit(LabelValue fileSizeUnit) {
		this.fileSizeUnit = fileSizeUnit;
	}

	public List<Object> getMandatory() {
		return mandatory;
	}

	public void setMandatory(List<Object> mandatory) {
		this.mandatory = mandatory;
	}

	public LabelValue getEnclosureType() {
		return enclosureType;
	}

	public void setEnclosureType(LabelValue enclosureType) {
		this.enclosureType = enclosureType;
	}

	public List<LabelValue> getAllowedType() {
		return allowedType;
	}

	public void setAllowedType(List<LabelValue> allowedType) {
		this.allowedType = allowedType;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public List<LabelValue> getDocumentsRecommended() {
		return documentsRecommended;
	}

	public void setDocumentsRecommended(List<LabelValue> documentsRecommended) {
		this.documentsRecommended = documentsRecommended;
	}
}
