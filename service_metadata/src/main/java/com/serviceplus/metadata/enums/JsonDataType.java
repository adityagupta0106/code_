package com.serviceplus.metadata.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JsonDataType {

	STRING("string"), NUMBER("number"), BOOLEAN("boolean"), OBJECT("object"), ARRAY("array"),
	ARRAY_STRING("arrayString"), TIMESTAMP("timestamp"), UUID("uuid"), EXPRESSION("expression"), NULL("null");

	private final String id;

	JsonDataType(String id) {
		this.id = id;
	}

	@JsonValue
	public String getId() {
		return id;
	}

	@JsonCreator
	public static JsonDataType fromId(String id) {
		if (id == null) {
			return null;
		}

		for (JsonDataType type : values()) {
			if (type.id.equalsIgnoreCase(id) || type.name().equalsIgnoreCase(id)) {
				return type;
			}
		}

		throw new IllegalArgumentException("Invalid JSON data type: " + id);
	}
}