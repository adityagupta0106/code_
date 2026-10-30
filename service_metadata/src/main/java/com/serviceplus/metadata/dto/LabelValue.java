package com.serviceplus.metadata.dto;

import java.io.Serializable;

public class LabelValue implements Serializable{
	private String label;
	private Object value;

	public LabelValue(String label, String value) {
		this.label=label;
		this.value=value;
	}

	public LabelValue() {
		
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
