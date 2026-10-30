package com.serviceplus.metadata.pattern.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Separator {

    HYPHEN("Hyphen", "HYPHEN", "-"),
    SLASH("Slash", "SLASH", "/"),
    UNDERSCORE("Underscore", "UNDERSCORE", "_"),
    BACKSLASH("Backslash", "BACKSLASH", "\\");

    private final String label;
    private final String value;
    private final String symbol;

    Separator(String label, String value, String symbol) {
        this.label = label;
        this.value = value;
        this.symbol = symbol;
    }

    public String getLabel() {
        return label;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getSymbol() {
        return symbol;
    }

    @JsonCreator
    public static Separator fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (Separator separator : values()) {
            if (separator.value.equalsIgnoreCase(value)
                    || separator.name().equalsIgnoreCase(value)
                    || separator.symbol.equals(value)) {
                return separator;
            }
        }
        throw new IllegalArgumentException("Invalid Separator value: " + value);
    }
}
