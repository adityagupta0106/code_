package com.serviceplus.metadata.correlationConfig.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SortingSequence {

	ASC("ASC"), DESC("DESC");

	private final String value;

	SortingSequence(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static SortingSequence fromValue(String value) {
		for (SortingSequence sequence : values()) {
			if (sequence.value.equalsIgnoreCase(value)) {
				return sequence;
			}
		}

		throw new IllegalArgumentException("Invalid SortingSequence: " + value);
	}
}