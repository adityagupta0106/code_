package com.serviceplus.formDesigner.FormApiExecution.Model;


import java.util.LinkedHashMap;
import java.util.Map;

public class ApiExecutionRequest {

    private String apiId;
    private Object apiDefinition;
    private Map<String, Object> attributeValues = new LinkedHashMap<>();
    private boolean validationRequired;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public Map<String, Object> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Map<String, Object> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public boolean isValidationRequired() {
        return validationRequired;
    }

    public void setValidationRequired(boolean validationRequired) {
        this.validationRequired = validationRequired;
    }

    public Object getApiDefinition() {
        return apiDefinition;
    }

    public void setApiDefinition(Object apiDefinition) {
        this.apiDefinition = apiDefinition;
    }
}
