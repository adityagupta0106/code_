package com.serviceplus.formDesigner.formrecommendation.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.repository.ApplicationTemplateRepository;
import com.serviceplus.formDesigner.repository.ApplicationTemplateStandaloneRepository;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.request.model.ApplicationTemplateStandalone;
import com.serviceplus.formDesigner.util.MessageConstants;

@Service
public class FormRecommendationService {

	@Autowired
	private ApplicationTemplateRepository applicationTemplateRepository;

	@Autowired
	private ApplicationTemplateStandaloneRepository applicationTemplateStandaloneRepository;

	@Autowired
	private ObjectMapper mapper;

	public ResponseEntity<?> getFormData(String formId) {

		try {
			Optional<Object> formJsonDataOpt = findFormJsonData(formId);

			if (formJsonDataOpt.isEmpty()) {
				throw new SPRuntimeError(MessageConstants.BAD_REQUEST.toString(), HttpStatus.BAD_REQUEST);
			}

			JSONObject formTemplateJson = processFormJsonData(formJsonDataOpt.get());

			Map<String, Object> responseJson = new HashMap<>();
			responseJson.put("formJsonData", mapper.readValue(formTemplateJson.toString(), Object.class));

			return ResponseEntity.ok(responseJson);

		} catch (SPRuntimeError ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Optional<Object> findFormJsonData(String formId) {

		ApplicationTemplate applicationTemplate = applicationTemplateRepository.findByFormId(formId);

		if (applicationTemplate != null) {
			return Optional.ofNullable(applicationTemplate.getFormJsonData());
		}

		return applicationTemplateStandaloneRepository.findById(formId).map(ApplicationTemplateStandalone::getFormJsonData);
	}
	
	private JSONObject processFormJsonData(Object formJsonData) throws Exception {

	    JSONObject formTemplateJson = new JSONObject(mapper.writeValueAsString(formJsonData));

	    processGlobalActions(formTemplateJson);
	    processFormElements(formTemplateJson);

	    return formTemplateJson;
	}
	
	private void processGlobalActions(JSONObject formTemplateJson) throws Exception {

	    JSONObject globalSetting = formTemplateJson.optJSONObject("globalSetting");

	    if (globalSetting == null) {
	        return;
	    }

	    JSONObject globalActions = globalSetting.optJSONObject("globalActions");

	    if (globalActions != null) {
	        changeApiSettingData(globalActions);
	    }
	}
	
	private void processFormElements(JSONObject formTemplateJson) throws Exception {

	    JSONArray formElements = formTemplateJson.optJSONArray("formElements");

	    if (formElements == null) {
	        return;
	    }

	    for (int i = 0; i < formElements.length(); i++) {

	        JSONObject formElement = formElements.optJSONObject(i);

	        if (formElement == null) {
	            continue;
	        }

	        JSONObject apiSettings =
	                formElement.optJSONObject("apiSettings");

	        if (apiSettings != null) {
	            changeApiSettingData(apiSettings);
	        }
	    }
	}

	public void changeApiSettingData(JSONObject apiSettingsJSON) throws Exception {
		JSONArray actionCreators = apiSettingsJSON.optJSONArray("actionCreators");
		for (int actCreObjIndex = 0; actCreObjIndex < actionCreators.length(); actCreObjIndex++) {
			JSONObject actionCreator = actionCreators.optJSONObject(actCreObjIndex);

			if (actionCreator != null) {
				String actionType = actionCreator.optString("actionType");
				if (actionType.equalsIgnoreCase("api")) {
					JSONObject apiDefinition = actionCreator.optJSONObject("apiDefinition");
					if (apiDefinition != null) {
						JSONObject request = apiDefinition.optJSONObject("request");
						String endpoint = request.optString("endpoint");
						if (!endpoint.isBlank()) {
							request.put("endpoint", "xxxxxxxxxx");
						}
						JSONArray headers = request.optJSONArray("headers");
						if (headers != null) {
							for (int headersIndex = 0; headersIndex < headers.length(); headersIndex++) {
								JSONObject headerJSONObj = headers.getJSONObject(headersIndex);
								String value = headerJSONObj.optString("value");
								if (!value.isBlank()) {
									headerJSONObj.put("value", "xxxxxxxxxx");
								}
							}
						}
						JSONArray params = request.optJSONArray("params");
						if (params != null) {
							for (int paramsIndex = 0; paramsIndex < params.length(); paramsIndex++) {
								JSONObject paramsJSONObj = params.getJSONObject(paramsIndex);
								String value = paramsJSONObj.optString("value");
								if (!value.isBlank()) {
									paramsJSONObj.put("value", "xxxxxxxxxx");
								}
							}
						}
						JSONObject body = request.optJSONObject("body");
						if (body != null) {
							String raw = body.optString("raw");
							if (!raw.isBlank()) {
								body.put("raw", "xxxxxxxxxx");
							}

							JSONArray formData = body.optJSONArray("formData");
							if (formData != null) {
								for (int formDataIndex = 0; formDataIndex < formData.length(); formDataIndex++) {
									JSONObject formDataObj = formData.getJSONObject(formDataIndex);
									String value = formDataObj.optString("value");
									if (!value.isBlank()) {
										formDataObj.put("value", "xxxxxxxxxx");
									}
								}
							}

							JSONArray urlEncoded = body.optJSONArray("urlEncoded");
							if (urlEncoded != null) {
								for (int urlEncodedIndex = 0; urlEncodedIndex < urlEncoded
										.length(); urlEncodedIndex++) {
									JSONObject urlEncodedObj = urlEncoded.getJSONObject(urlEncodedIndex);
									String value = urlEncodedObj.optString("value");
									if (!value.isBlank()) {
										urlEncodedObj.put("value", "xxxxxxxxxx");
									}
								}
							}

							JSONObject authorization = request.optJSONObject("authorization");
							if (authorization != null) {
								JSONObject apiKey = authorization.optJSONObject("apiKey");
								if (apiKey != null) {
									String key = apiKey.optString("key");
									String value = apiKey.optString("value");
									if (!key.isBlank()) {
										apiKey.put("key", "xxxxxxxxxx");
									}
									if (!value.isBlank()) {
										apiKey.put("value", "xxxxxxxxxx");
									}
								}

								JSONObject bearer = authorization.optJSONObject("bearer");
								if (bearer != null) {
									String token = bearer.optString("token");
									if (!token.isBlank()) {
										bearer.put("token", "xxxxxxxxxx");
									}
								}

								JSONObject basic = authorization.optJSONObject("basic");
								if (basic != null) {
									String username = basic.optString("username");
									if (!username.isBlank()) {
										basic.put("username", "xxxxxxxxxx");
									}
									String password = basic.optString("password");
									if (!password.isBlank()) {
										basic.put("password", "xxxxxxxxxx");
									}
								}
							}
						}

					}
				}
			}
		}
	}

}
