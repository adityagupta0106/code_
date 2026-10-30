package com.serviceplus.metadata.pattern.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SpgdAttributeCode {

	LGD_LOCATION_CODE(1, "LGD_LOCATION_CODE"),

	LGD_LOCATION_NAME(2, "LGD_LOCATION_NAME");

	private final Integer code;
	private final String value;

	SpgdAttributeCode(Integer code, String value) {

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
	public static SpgdAttributeCode fromCode(Integer code) {

		for (SpgdAttributeCode type : values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}

		throw new IllegalArgumentException("Invalid SpgdAttributeCode: " + code);
	}
}