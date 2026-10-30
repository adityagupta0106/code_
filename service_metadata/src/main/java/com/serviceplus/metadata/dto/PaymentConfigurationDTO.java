package com.serviceplus.metadata.dto;

import java.util.List;
import java.util.Map;

public class PaymentConfigurationDTO {

    private List<PaymentCharge> paymentCharges;

    private List<PaymentGatewayConfiguration> paymentGatewayConfigurations;

    private List<TaskPaymentMapping> taskPaymentMappings;

    public List<PaymentCharge> getPaymentCharges() {
        return paymentCharges;
    }

    public void setPaymentCharges(List<PaymentCharge> paymentCharges) {
        this.paymentCharges = paymentCharges;
    }

    public List<PaymentGatewayConfiguration> getPaymentGatewayConfigurations() {
        return paymentGatewayConfigurations;
    }

    public void setPaymentGatewayConfigurations(
            List<PaymentGatewayConfiguration> paymentGatewayConfigurations) {
        this.paymentGatewayConfigurations = paymentGatewayConfigurations;
    }

    public List<TaskPaymentMapping> getTaskPaymentMappings() {
        return taskPaymentMappings;
    }

    public void setTaskPaymentMappings(
            List<TaskPaymentMapping> taskPaymentMappings) {
        this.taskPaymentMappings = taskPaymentMappings;
    }

    // =========================================================
    // PAYMENT CHARGE
    // =========================================================

    public static class PaymentCharge {

        private Long chargeDetailId;
        private String templateName;
        private Boolean defaultCharge;
        private Boolean serviceUnitWise;
        private List<PaymentMode> paymentModes;
        private List<ChargeType> chargeTypeList;
        private List<LabelValue> chargeDefinedFor;
        private List<ClosedUserType> closedUserType;

        public Long getChargeDetailId() {
            return chargeDetailId;
        }

        public void setChargeDetailId(Long chargeDetailId) {
            this.chargeDetailId = chargeDetailId;
        }

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public Boolean getDefaultCharge() {
            return defaultCharge;
        }

        public void setDefaultCharge(Boolean defaultCharge) {
            this.defaultCharge = defaultCharge;
        }

        public Boolean getServiceUnitWise() {
            return serviceUnitWise;
        }

        public void setServiceUnitWise(Boolean serviceUnitWise) {
            this.serviceUnitWise = serviceUnitWise;
        }

        public List<PaymentMode> getPaymentModes() {
            return paymentModes;
        }

        public void setPaymentModes(List<PaymentMode> paymentModes) {
            this.paymentModes = paymentModes;
        }

        public List<ChargeType> getChargeTypeList() {
            return chargeTypeList;
        }

        public void setChargeTypeList(List<ChargeType> chargeTypeList) {
            this.chargeTypeList = chargeTypeList;
        }

        public List<LabelValue> getChargeDefinedFor() {
            return chargeDefinedFor;
        }

        public void setChargeDefinedFor(List<LabelValue> chargeDefinedFor) {
            this.chargeDefinedFor = chargeDefinedFor;
        }

        public List<ClosedUserType> getClosedUserType() {
            return closedUserType;
        }

        public void setClosedUserType(List<ClosedUserType> closedUserType) {
            this.closedUserType = closedUserType;
        }
    }

    // =========================================================
    // PAYMENT MODE
    // =========================================================

    public static class PaymentMode {

        private String label;
        private String value;
        private Boolean onlineFlag;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getOnlineFlag() {
            return onlineFlag;
        }

        public void setOnlineFlag(Boolean onlineFlag) {
            this.onlineFlag = onlineFlag;
        }
    }

    // =========================================================
    // CHARGE TYPE
    // =========================================================

    public static class ChargeType {

        private String name;
        private Double amount;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }

    // =========================================================
    // LABEL VALUE
    // =========================================================

    public static class LabelValue {

        private String label;
        private String value;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    // =========================================================
    // CLOSED USER TYPE
    // =========================================================

    public static class ClosedUserType {

        private Integer value;
        private String label;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    // =========================================================
    // PAYMENT GATEWAY CONFIGURATION
    // =========================================================

    public static class PaymentGatewayConfiguration {

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
    }

    // =========================================================
    // ADDITIONAL DATA
    // =========================================================

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

    // =========================================================
    // GATEWAY MAPPING
    // =========================================================

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

    // =========================================================
    // TASK PAYMENT MAPPING
    // =========================================================

    public static class TaskPaymentMapping {

        private String taskId;
        private Object payment;

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public Object getPayment() {
            return payment;
        }

        public void setPayment(Object payment) {
            this.payment = payment;
        }
    }
}