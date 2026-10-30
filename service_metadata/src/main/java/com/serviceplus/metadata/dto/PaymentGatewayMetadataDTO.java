package com.serviceplus.metadata.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public class PaymentGatewayMetadataDTO {

    private String gatewayCode;
    private Boolean enabled;
    private AdditionalData additionalData;
    private List<GatewayMapping> mappings;

    public String getGatewayCode() {
        return gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public AdditionalData getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(AdditionalData additionalData) {
        this.additionalData = additionalData;
    }

    public List<GatewayMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<GatewayMapping> mappings) {
        this.mappings = mappings;
    }

    public static class AdditionalData {

        private String applicabilityScope;
        private Boolean enableRevalidation;
        private Boolean refundAllowed;
        private List<String> revalidationModes;
        private Map<String, Integer> revalidationPeriods;

        public String getApplicabilityScope() {
            return applicabilityScope;
        }

        public void setApplicabilityScope(String applicabilityScope) {
            this.applicabilityScope = applicabilityScope;
        }

        public Boolean getEnableRevalidation() {
            return enableRevalidation;
        }

        public void setEnableRevalidation(Boolean enableRevalidation) {
            this.enableRevalidation = enableRevalidation;
        }

        public Boolean getRefundAllowed() {
            return refundAllowed;
        }

        public void setRefundAllowed(Boolean refundAllowed) {
            this.refundAllowed = refundAllowed;
        }

        public List<String> getRevalidationModes() {
            return revalidationModes;
        }

        public void setRevalidationModes(List<String> revalidationModes) {
            this.revalidationModes = revalidationModes;
        }

        public Map<String, Integer> getRevalidationPeriods() {
            return revalidationPeriods;
        }

        public void setRevalidationPeriods(
                Map<String, Integer> revalidationPeriods) {

            this.revalidationPeriods = revalidationPeriods;
        }
    }

    public static class GatewayMapping {

        private String mode;
        private String nodeCode;
        private String sourceType;
        private String sourceValue;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getNodeCode() {
            return nodeCode;
        }

        public void setNodeCode(String nodeCode) {
            this.nodeCode = nodeCode;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getSourceValue() {
            return sourceValue;
        }

        public void setSourceValue(String sourceValue) {
            this.sourceValue = sourceValue;
        }
    }
}