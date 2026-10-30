package com.serviceplus.metadata.enums;

public enum NonAdoptedSubmissionPolicy {

    ALLOW_NON_ADOPTED_SUBMISSION("YES"),
    BLOCK_NON_ADOPTED_SUBMISSION("NO");

    private final String alias;

    NonAdoptedSubmissionPolicy(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public static NonAdoptedSubmissionPolicy fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BLOCK_NON_ADOPTED_SUBMISSION;
        }
        String normalized = value.trim().toUpperCase();
        if ("YES".equals(normalized) || "Y".equals(normalized) || "ALLOW_NON_ADOPTED_SUBMISSION".equals(normalized)) {
            return ALLOW_NON_ADOPTED_SUBMISSION;
        }
        if ("NO".equals(normalized) || "N".equals(normalized) || "BLOCK_NON_ADOPTED_SUBMISSION".equals(normalized)) {
            return BLOCK_NON_ADOPTED_SUBMISSION;
        }
        throw new IllegalArgumentException("Invalid NonAdoptedSubmissionPolicy value: " + value + ". Expected YES/NO or ALLOW_NON_ADOPTED_SUBMISSION/BLOCK_NON_ADOPTED_SUBMISSION");
    }

    public boolean isAllow() {
        return this == ALLOW_NON_ADOPTED_SUBMISSION;
    }
}
