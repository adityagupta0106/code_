package com.serviceplus.metadata.correlationConfig.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SetType {
	SERVICE("S"), EXTERNAL_DATA("E");

	private final String value;

	SetType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static SetType fromValue(String value) {
		for (SetType sequence : values()) {
			if (sequence.value.equalsIgnoreCase(value)) {
				return sequence;
			}
		}

		throw new IllegalArgumentException("Invalid SortingSequence: " + value);
	}
}
