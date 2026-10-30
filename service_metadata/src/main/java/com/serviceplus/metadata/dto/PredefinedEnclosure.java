package com.serviceplus.metadata.dto;

import java.util.List;

public class PredefinedEnclosure {
	private String name;
	private FileDTO file;
	private List<Object> mandatory;// need to check
	private String sortOrder;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileDTO getFile() {
		return file;
	}

	public void setFile(FileDTO file) {
		this.file = file;
	}

	public List<Object> getMandatory() {
		return mandatory;
	}

	public void setMandatory(List<Object> mandatory) {
		this.mandatory = mandatory;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
}
