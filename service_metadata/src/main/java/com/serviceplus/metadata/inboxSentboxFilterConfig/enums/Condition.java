package com.serviceplus.metadata.inboxSentboxFilterConfig.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Condition {

    AND("AND"),
    OR("OR");

    private final String value;

    Condition(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Condition fromValue(String value) {
        for (Condition condition : values()) {
            if (condition.value.equalsIgnoreCase(value)) {
                return condition;
            }
        }

        throw new IllegalArgumentException("Invalid Condition: " + value);
    }
}