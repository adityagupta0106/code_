package com.serviceplus.metadata.pattern.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CallbackBehaviour {

	REGENERATE(1, "REGENERATE"),

	EXISTING_NUMBER(2, "EXISTING_NUMBER");

	private final Integer code;
	private final String value;

	CallbackBehaviour(Integer code, String value) {

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
	public static CallbackBehaviour fromCode(Integer code) {

		for (CallbackBehaviour behaviour : values()) {
			if (behaviour.code.equals(code)) {
				return behaviour;
			}
		}

		throw new IllegalArgumentException("Invalid CallbackBehaviour: " + code);
	}
}