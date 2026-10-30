package com.serviceplus.metadata.inboxSentboxFilterConfig.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FilterType {

    INBOX("I"),
    SENTBOX("S"),
    BOTH("B");

    private final String value;

    FilterType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FilterType fromValue(String value) {
        for (FilterType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Output Type: " + value);
    }
}