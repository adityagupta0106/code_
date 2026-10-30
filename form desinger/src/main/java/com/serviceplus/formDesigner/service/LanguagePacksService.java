package com.serviceplus.formDesigner.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.enums.FormStatus;
import com.serviceplus.formDesigner.repository.ApplicationTemplateRepository;
import com.serviceplus.formDesigner.repository.LanguageMasterRepository;
import com.serviceplus.formDesigner.repository.LanguagePacksRepository;
import com.serviceplus.formDesigner.repository.LanguagesRepository;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.request.model.LanguageMaster;
import com.serviceplus.formDesigner.request.model.LanguagePacks;
import com.serviceplus.formDesigner.request.model.Languages;
import com.serviceplus.formDesigner.util.ApplicationConstants;
import com.serviceplus.formDesigner.util.MessageConstants;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class LanguagePacksService {

	@Autowired private ApplicationTemplateService applicationTemplateService;
	
	@Autowired private LanguageMasterService languageMasterService;
	
	@Autowired private LanguagePacksRepository languagePacksRepository;
	
	@Autowired private LanguagesRepository languagesRepository;

	@Autowired private LanguageMasterRepository languageMasterRepository;
	
	@Autowired private ApplicationTemplateRepository applicationTemplateRepository;
	
	public Object getLanguagePackByFormId(String formId,HttpServletResponse response) {
		Map<String,Object> responseBody =  new HashMap<String, Object>();
		try {
			List<ApplicationTemplate> applicationTemplate =  applicationTemplateRepository.findByFormIdAndStatus(formId, FormStatus.PUBLISHED.getValue());
			if(applicationTemplate != null && !applicationTemplate.isEmpty()) {
				responseBody.put("formId", applicationTemplate.get(0).getFormId());
				setAllLanguage(responseBody);
				setAllAttributes(responseBody, applicationTemplate.get(0));
			}else {
				responseBody.put("message", MessageConstants.RECORD_NOT_FOUND);
				responseBody.put("status", HttpStatus.OK.value());
			}
		}catch(Exception e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseBody.put("message", MessageConstants.INTERNAL_ERROR);
		}
		return responseBody;
	}
	
	private void setAllLanguage(Map<String,Object> responseBody) {
		Map<String,Object> language = new HashMap<String, Object>();
		List<LanguageMaster> languageMasterList = languageMasterService.getAllLanguageMaster();
		for(LanguageMaster languageMaster: languageMasterList) {
			language.put(languageMaster.getLangShortName(), languageMaster.getLangName());
		}
		
		responseBody.put("language", language);
	}
	
	private void setAllAttributes(Map<String,Object> responseBody,ApplicationTemplate applicationTemplate) {
		List<Object> formData = new ArrayList<Object>();
		Map<String,Object> data = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
	        String stringFormTemplateObject = objectMapper.writeValueAsString(applicationTemplate);
	        JSONObject jsonFormTemplateObject = new JSONObject(stringFormTemplateObject);
	        
//			JSONObject jsonFormTemplateObject = new JSONObject(applicationTemplate.getFormJsonData().toString());
			
			 JSONObject formJsonData = jsonFormTemplateObject.getJSONObject("formJsonData");
		     JSONArray formElements = formJsonData.getJSONArray("formElements");
		        
			 for (int i = 0; i < formElements.length(); i++) {
	        	 JSONObject element = formElements.getJSONObject(i);
	        	 String uniqueId = element.optString("uniqueId");
	        	 JSONObject displaySettings = element.optJSONObject("displaySettings");
	        	 
	        	 String label = displaySettings.getString("label");
	        	 
//	        	 JSONObject labelObject = displaySettings.optJSONObject("label");
//	        	 String label = labelObject.getString("value");
	        	 
	        	 data = new HashMap<String, Object>();
	        	 data.put("S.No", i+1);
	        	 data.put("Label Value", label);
	        	 data.put("uid", uniqueId);
	        	 data.put("Translated Value", "");
	        	 
	        	 formData.add(data);
			 }
			 responseBody.put("formData", formData);
		}catch (Exception e) {
			e.printStackTrace();
			responseBody.put("message", MessageConstants.INTERNAL_ERROR);
			responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
	
	public Map<String, Object> getLanguagePacks(String id,HttpServletResponse httpResponse) {
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> status = new HashMap<String, Object>();
		try {
			response.put("id", id);
			response.put("createdBy", "");
			response.put("data", "");
			if(!isValidLanguageId(id)) {
				httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				 status.put("code", 404);
				 status.put("message", MessageConstants.INVALID_ID);
				 response.put("status", status);
				 return response;
			}
			response.put("languageMaster", languageMasterService.getLanguagePacksMaster().getData());
			Optional<LanguagePacks> languagesPacksList = languagePacksRepository.findById(id);
			ObjectMapper objectMapper = new ObjectMapper();
			if (languagesPacksList.isPresent()) {
			    Map<String, Object> responseMap = languagesPacksList.map(lp -> {
			        Map<String, Object> map = new HashMap<>();
			        map.put("id", lp.getId());
			        try {
			            map.put("data", objectMapper.readValue(lp.getData(), Map.class));
			        } catch (JsonProcessingException e) {
			            e.printStackTrace();
			            httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			            status.put("code", 500);
			            status.put("message", e.getMessage());
			        }
			        return map;
			    }).orElse(Collections.emptyMap());
			    status.put("code", 200);
	            status.put("message", "");
			    response.putAll(responseMap);
			} else {
				Optional<LanguagePacks> defaulLanguagesPacksList = languagePacksRepository.findById(ApplicationConstants.DEFAULT_LANGUAGE_PACK);
				response.put("data", objectMapper.readValue(defaulLanguagesPacksList.get().getData(), Map.class));
			    status.put("code", 404);
			    status.put("message", MessageConstants.RECORD_NOT_FOUND);
			}
			response.put("status", status);
		} catch (Exception e) {
			httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			status.put("code", 500);
			status.put("message", e.getMessage());
			response.put("status", status);
		}
		return response;
	}
	public boolean isValidLanguagePackData(String data,List<String> invalidKeys ) {
		Boolean isValidMasterKey = true;
		List<String> masterKeyList = Arrays.asList(languageMasterService.getLanguagePacksMaster().getData());
		try {
			JSONObject jsonObject = new JSONObject(data);
			Iterator<String> jsonKeys = jsonObject.keys();
			while (jsonKeys.hasNext()) {
	            String key = jsonKeys.next();
	            if (!masterKeyList.contains(key)) {
	                invalidKeys.add(key);
	                isValidMasterKey = false;
	            }
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return isValidMasterKey;
	}
	public boolean isValidLanguageId(String str) {
		return str != null && str.length() == 2 && Character.isLetter(str.charAt(0)) && Character.isLetter(str.charAt(0));
	}

	public Map<String, Object> addLanguagePacks(LanguagePacks languagePacks,HttpServletResponse httpResponse) {
		Map<String, Object> status = new HashMap<String, Object>();
		Optional<Languages> languageList = Optional.empty();
		List<String> invalidKeys = new ArrayList<>();
		try {
			status.put("message", "");
			if(!isValidLanguageId(languagePacks.getId())){
				httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				 status.put("code", 404);
				 status.put("message", MessageConstants.INVALID_ID);
				 return status;
			}
			languageList = languagesRepository.findById(languagePacks.getId());

			if(languageList.isEmpty()) {
				httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				 status.put("code", 404);
				 status.put("message", MessageConstants.INVALID_ID);
				 return status;
			}
			if(!isValidLanguagePackData(languagePacks.getData(),invalidKeys)){
				httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				 status.put("code", 404);
				 status.put("message", MessageConstants.INVALID_LANGUAGE_PACK_MASTER_KEY);
				 status.put("invalidKeys", invalidKeys);
				 return status;
			}
			Optional<LanguagePacks> dbLanguagePackList = languagePacksRepository.findById(languagePacks.getId());
			if(!dbLanguagePackList.isEmpty()){
				languagePacks.setModifyOn(new Date());
			}else {
				languagePacks.setCreatedOn(new Date());
			}
			LanguageMaster languageMaster = new LanguageMaster();
			languageMaster.setId(languageList.get().getId());
			languageMaster.setLangName(languageList.get().getValue());
			languageMaster.setLangShortName(languageList.get().getId());
			languageMasterRepository.save(languageMaster);
			languagePacksRepository.save(languagePacks);
			 status.put("code", 200);
			 status.put("message", MessageConstants.DATA_SAVE);
			 return status;
			
		}catch (Exception e) {
			e.printStackTrace();
			httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			status.put("code", 500);
			status.put("message", e.getMessage());
		}
		return status;
	}
}
