package com.serviceplus.metadata.correlationConfig.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AttributeSource {

	F("F"), S("S");

	private final String value;

	AttributeSource(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static AttributeSource fromValue(String value) {
		for (AttributeSource sequence : values()) {
			if (sequence.value.equalsIgnoreCase(value)) {
				return sequence;
			}
		}

		throw new IllegalArgumentException("Invalid SortingSequence: " + value);
	}
}