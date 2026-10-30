package com.serviceplus.tracking.correlationapplication.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CorrelationLookupResponse {

	private final Map<String, Object> values = new LinkedHashMap<>();
	private final Map<String, List<Map<String, Object>>> fieldsets = new LinkedHashMap<>();

	public Map<String, Object> getValues() {
		return values;
	}

	public Map<String, List<Map<String, Object>>> getFieldsets() {
		return fieldsets;
	}

}