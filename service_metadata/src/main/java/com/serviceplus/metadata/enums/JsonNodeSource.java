package com.serviceplus.metadata.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JsonNodeSource {

	STATIC("Static"), FORM_FIELD("Form Field"), SYSTEM_VAR("System Var"), EXPRESSION("Expression"),
	DOCUMENT("Document");

	private final String id;

	JsonNodeSource(String id) {
		this.id = id;
	}

	@JsonValue
	public String getId() {
		return id;
	}

	@JsonCreator
	public static JsonNodeSource fromId(String id) {
		if (id == null) {
			return null;
		}

		for (JsonNodeSource source : values()) {
			if (source.id.equalsIgnoreCase(id) || source.name().equalsIgnoreCase(id)) {
				return source;
			}
		}

		throw new IllegalArgumentException("Invalid JSON node source: " + id);
	}
}