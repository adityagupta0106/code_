package com.serviceplus.metadata.enums;

public enum SlaStartPolicy {
    ON_SUBMISSION,
    ON_ADOPTION;

    public static SlaStartPolicy fromString(String val) {
        if (val == null) {
            return ON_ADOPTION;
        }
        for (SlaStartPolicy policy : SlaStartPolicy.values()) {
            if (policy.name().equalsIgnoreCase(val.trim())) {
                return policy;
            }
        }
        return ON_ADOPTION;
    }
}
