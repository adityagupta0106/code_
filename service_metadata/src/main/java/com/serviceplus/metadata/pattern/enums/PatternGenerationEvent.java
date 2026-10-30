package com.serviceplus.metadata.pattern.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PatternGenerationEvent {

	UNIQUE_FOR_EACH_APPLICATION(1, "UNIQUE_FOR_EACH_APPLICATION"),

	UNIQUE_FOR_EACH_DOCUMENT(2, "UNIQUE_FOR_EACH_DOCUMENT");

	private final Integer code;
	private final String value;

	PatternGenerationEvent(Integer code, String value) {

		this.code = code;
		this.value = value;
	}

	@JsonValue
	public Integer getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	@JsonCreator
	public static PatternGenerationEvent fromCode(Integer code) {

		for (PatternGenerationEvent event : values()) {
			if (event.code.equals(code)) {
				return event;
			}
		}

		throw new IllegalArgumentException("Invalid PatternGenerationEvent code: " + code);
	}
}