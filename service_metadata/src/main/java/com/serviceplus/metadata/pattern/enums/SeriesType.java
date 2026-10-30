package com.serviceplus.metadata.pattern.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SeriesType {

    NUMERIC(1, "NUMERIC", "Numeric"),
    ALPHABETIC(2, "ALPHABETIC", "Alphabetic"),
    ALPHANUMERIC(3, "ALPHANUMERIC", "Alphanumeric");

    private final Integer code;
    private final String value;
    private final String label;

    SeriesType(Integer code, String value, String label) {
        this.code = code;
        this.value = value;
        this.label = label;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    @JsonValue
    public Object toJson() {
        return code;
    }

    @JsonCreator
    public static SeriesType fromJson(Object input) {
        if (input == null) {
            return null;
        }
        if (input instanceof Integer intVal) {
            return fromCode(intVal);
        }
        String str = input.toString().trim();
        try {
            int code = Integer.parseInt(str);
            return fromCode(code);
        } catch (NumberFormatException ignored) {
        }

        for (SeriesType type : values()) {
            if (type.value.equalsIgnoreCase(str) || type.name().equalsIgnoreCase(str)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Invalid SeriesType code or value: " + input);
    }

    public static SeriesType fromCode(Integer code) {
        for (SeriesType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Invalid SeriesType code: " + code);
    }
}
