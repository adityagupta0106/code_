package com.serviceplus.formDesigner.FormApiExecution.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.serviceplus.formDesigner.util.ApplicationConstants.FORM_API_CALL_TYPE_SERVER;
import static java.util.Objects.isNull;

@Service
public class ServerApiSanitizer {

    private static final Logger LOGGER = LoggerFactory.getLogger("webServiceCallLogger");

    @SuppressWarnings("unchecked")
    public Object sanitize(Object node) {

        switch (node) {
            case null -> {
                return null;
            }
            case Map<?, ?> map -> {

                Map<String, Object> object = (Map<String, Object>) node;

                if ("api".equalsIgnoreCase(String.valueOf(object.get("actionType"))) && object.containsKey("apiDefinition")) {
                    sanitizeApiDefinition(object);
                }

                for (Object value : object.values()) {
                    sanitize(value);
                }
            }
            case List<?> objects -> {

                for (Object value : objects) {
                    sanitize(value);
                }
            }
            default -> {
            }
        }

        return node;
    }

    @SuppressWarnings("unchecked")
    private void sanitizeApiDefinition(Map<String, Object> action) {

        LOGGER.info("Sanitizing API [{}].", action.getOrDefault("id", "UNKNOWN"));
        Map<String, Object> apiDefinition = (Map<String, Object>) action.get("apiDefinition");

        if (isNull(apiDefinition)) {
            LOGGER.info("API definition not found.");
            return;
        }

        Map<String, Object> request = (Map<String, Object>) apiDefinition.get("request");

        if (isNull(request)) {
            LOGGER.info("API request definition not found.");
            return;
        }

        String callType = String.valueOf(request.get("callType"));

        if (!FORM_API_CALL_TYPE_SERVER.equalsIgnoreCase(callType)) {
            LOGGER.info("API [{}] is client side. Skipping sanitization.", action.getOrDefault("id", "UNKNOWN"));
            return;
        }

        /*
         * Remove endpoint
         */
        request.put("endpoint", "");
        request.put("method","");

        /*
         * Remove static header values
         */
        sanitizeMappings((List<Map<String, Object>>) request.get("headers"));

        /*
         * Remove static query params
         */
        sanitizeMappings((List<Map<String, Object>>) request.get("params"));

        /*
         * Remove request body
         */
        sanitizeBody((Map<String, Object>) request.get("body"));

        /*
         * Remove authorization values
         */
        sanitizeAuthorization((Map<String, Object>) request.get("authorization"));

        LOGGER.info("Server API [{}] sanitized successfully.", action.getOrDefault("id", "UNKNOWN"));
    }

    private void sanitizeMappings(List<Map<String, Object>> mappings) {

        if (mappings == null) {
            return;
        }

        for (Map<String, Object> mapping : mappings) {
            mapping.put("label", "");
            mapping.put("value", "");
        }
    }

    @SuppressWarnings("unchecked")
    private void sanitizeBody(Map<String, Object> body) {

        if (body == null) {
            LOGGER.info("Request body not configured.");
            return;
        }

        body.put("raw", "");

        sanitizeMappings((List<Map<String, Object>>) body.get("formData"));

        sanitizeMappings((List<Map<String, Object>>) body.get("urlEncoded"));
    }

    @SuppressWarnings("unchecked")
    private void sanitizeAuthorization(Map<String, Object> authorization) {

        if (isNull(authorization)) {
            LOGGER.info("Authorization not configured.");
            return;
        }

        LOGGER.info("Sanitizing authorization.");

        Map<String, Object> apiKey = (Map<String, Object>) authorization.get("apiKey");

        if (apiKey != null) {
            apiKey.put("key", "");
            apiKey.put("value", "");
        }

        Map<String, Object> bearer = (Map<String, Object>) authorization.get("bearer");

        if (bearer != null) {
            bearer.put("token", "");
        }

        Map<String, Object> basic = (Map<String, Object>) authorization.get("basic");

        if (basic != null) {
            basic.put("username", "");
            basic.put("password", "");
        }
    }
}