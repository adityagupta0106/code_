package com.serviceplus.metadata.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormBlankJsonDTO {

	private final Map<String, Object> formData = new LinkedHashMap<>();

	public void setValue(String uniqueId, Object value) {
		formData.put(uniqueId, value);
	}

	public Map<String, Object> getFormData() {
		return formData;
	}
}
