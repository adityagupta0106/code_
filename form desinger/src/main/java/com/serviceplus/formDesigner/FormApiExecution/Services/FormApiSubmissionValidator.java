package com.serviceplus.formDesigner.FormApiExecution.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.FormApiExecution.Model.ValidationJwtClaims;
import com.serviceplus.formDesigner.FormApiExecution.Validation.ValidationJwtService;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

import static com.serviceplus.formDesigner.util.ApplicationConstants.FORM_API_CALL_TYPE_SERVER;

@Service
public class FormApiSubmissionValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger("webServiceCallLogger");

    private final ValidationJwtService validationJwtService;

    private final ObjectMapper objectMapper;

    public FormApiSubmissionValidator(ValidationJwtService validationJwtService, ObjectMapper objectMapper) {
        this.validationJwtService = validationJwtService;
        this.objectMapper = objectMapper;
    }

    public void validate(Object formDefinition, Map<String, Object> submittedData, Map<String, String> validationTokens) throws Exception {

        JSONObject formJson = new JSONObject(objectMapper.writeValueAsString(formDefinition));
        JSONArray formElements = formJson.optJSONArray("formElements");

        if (formElements != null) {
            validateFormElements(formElements, submittedData, validationTokens);
        }

        JSONObject globalSetting = formJson.optJSONObject("globalSetting");

        if (globalSetting != null) {

            JSONObject globalActions = globalSetting.optJSONObject("globalActions");

            if (globalActions != null) {
                JSONArray actionCreators = globalActions.optJSONArray("actionCreators");
                validateActionCreators(actionCreators, submittedData, validationTokens);
            }
        }
    }

    private void validateFormElements(JSONArray formElements, Map<String, Object> submittedData, Map<String, String> validationTokens) throws JSONException {

        for (int i = 0; i < formElements.length(); i++) {

            JSONObject element = formElements.getJSONObject(i);
            JSONObject apiSettings = element.optJSONObject("apiSettings");

            if (apiSettings == null) {
                continue;
            }

            JSONArray actionCreators = apiSettings.optJSONArray("actionCreators");

            validateActionCreators(actionCreators, submittedData, validationTokens);
        }
    }

    private void validateActionCreators(JSONArray actionCreators, Map<String, Object> submittedData, Map<String, String> validationTokens) throws JSONException {

        if (actionCreators == null) {
            return;
        }

        for (int i = 0; i < actionCreators.length(); i++) {

            JSONObject action = actionCreators.getJSONObject(i);

            if (!"api".equalsIgnoreCase(action.optString("actionType"))) {
                continue;
            }

            validateApiAction(action, submittedData, validationTokens);
        }
    }

    private void validateApiAction(JSONObject action, Map<String, Object> submittedData, Map<String, String> validationTokens) throws JSONException {

        String apiId = action.getString("id");

        JSONObject apiDefinition = action.getJSONObject("apiDefinition");

        JSONObject request = apiDefinition.getJSONObject("request");

        String callType = request.getString("callType");

        boolean serverValidation = request.optBoolean("serverValidation", false);

        if(!FORM_API_CALL_TYPE_SERVER.equals(callType) || !serverValidation){
            return;
        }

        JSONArray mappings = apiDefinition.getJSONArray("dataMapping");

        boolean apiTriggered = false;

        for (int i = 0; i < mappings.length(); i++) {

            JSONObject mapping = mappings.getJSONObject(i);
            String attributeId = mapping.getString("mapWith");

            if (submittedData.containsKey(attributeId)) {
                apiTriggered = true;
                break;
            }
        }

        if (!apiTriggered) {
            return;
        }

        String token = validationTokens.get(apiId);

        if (token == null || token.isBlank()) {
            throw new SPRuntimeError("Validation token missing for API : " + apiId, HttpStatus.BAD_REQUEST);
        }

        ValidationJwtClaims jwtClaims = validationJwtService.parse(token);

        for (int i = 0; i < mappings.length(); i++) {

            JSONObject mapping = mappings.getJSONObject(i);

            String attributeId = mapping.getString("mapWith");

            if (!submittedData.containsKey(attributeId)) {
                continue;
            }

            ValidationJwtClaims.ValidationAttributeClaim attribute = jwtClaims.getAttributes().get(attributeId);

            if (attribute == null) {
                throw new SPRuntimeError("Validation data not found for attribute : " + attributeId, HttpStatus.BAD_REQUEST);
            }

            Object submittedValue = submittedData.get(attributeId);

            /*
             * Textbox / Label / Readonly field
             * No allowed values => overwrite submitted value.
             */
            if (attribute.getAllowedValues() == null || attribute.getAllowedValues().isEmpty()) {

                if (attribute.getDynamicData() != null && !attribute.getDynamicData().isEmpty()) {

                    Object actualValue = attribute.getDynamicData().getFirst().get("value");
                    LOGGER.info("Overriding submitted value for attribute [{}] from [{}] to [{}]", attributeId, submittedValue, actualValue);
                    submittedData.put(attributeId, actualValue);
                }

                continue;
            }

            /*
             * Select / Radio / Checkbox / Multi Select
             */

            if (submittedValue instanceof Map<?, ?> valueMap) {
                submittedValue = valueMap.get("value");
            }

            boolean valid;

            if (submittedValue instanceof Iterable<?> values) {

                valid = true;

                for (Object value : values) {

                    if (!(Boolean)validationJwtService.validate(token, attributeId, value)) {
                        valid = false;
                        break;
                    }
                }

            } else {
                valid = (Boolean)validationJwtService.validate(token, attributeId, submittedValue);
            }


            if (!valid) {
                throw new SPRuntimeError("Invalid value submitted for attribute : " + attributeId, HttpStatus.BAD_REQUEST);
            }
        }
    }
}
