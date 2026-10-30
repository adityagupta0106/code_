package com.serviceplus.formDesigner.FormApiExecution.Model;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormApiServerActionRequest {

    private String formId;

    private String apiId;

    private Map<String, Object> attributeValues = new LinkedHashMap<>();

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Map<String, Object> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Map<String, Object> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }
}
