package com.serviceplus.formDesigner.FormApiExecution.Model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValidationJwtClaims {

    private String apiId;

    private Map<String, ValidationAttributeClaim> attributes = new LinkedHashMap<>();

    public static class ValidationAttributeClaim {

        private String dataType;

        private List<Object> allowedValues = new ArrayList<>();

        private List<Map<String, Object>> dynamicData = new ArrayList<>();

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public List<Object> getAllowedValues() {
            return allowedValues;
        }

        public void setAllowedValues(List<Object> allowedValues) {
            this.allowedValues = allowedValues;
        }

        public List<Map<String, Object>> getDynamicData() {
            return dynamicData;
        }

        public void setDynamicData(List<Map<String, Object>> dynamicData) {
            this.dynamicData = dynamicData;
        }
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public Map<String, ValidationAttributeClaim> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, ValidationAttributeClaim> attributes) {
        this.attributes = attributes;
    }
}
