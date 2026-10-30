package com.serviceplus.formDesigner.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.request.model.IApplicationTemplate;
import com.serviceplus.formDesigner.util.ApplicationConstants;
import com.serviceplus.formDesigner.util.MessageConstants;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ApplicationTemplateValidationService {
	
	public Boolean isLastDateAfterStartDate(String startDateTimeStr, String lastDateTimeStr,String strFormat) {
	    try {
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(strFormat);
	    	LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr, formatter);
	    	LocalDateTime lastDateTime = LocalDateTime.parse(lastDateTimeStr, formatter);
	        return lastDateTime.isAfter(startDateTime);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public Boolean checkApplicationTemplateValidation(IApplicationTemplate applicationTemplate,Map<String, Object> response,HttpServletResponse httpServletResponse) {
		Boolean isValid = true;
		ObjectMapper objectMapper = new ObjectMapper();
        String stringFormTemplateObject;
        String startDateTime =  null;
        String lastDateTime =  null;
		try {
			
			stringFormTemplateObject = objectMapper.writeValueAsString(applicationTemplate.getFormJsonData());
			JSONObject jsonFormTemplateObject = new JSONObject(stringFormTemplateObject);
			JSONObject globalSetting = jsonFormTemplateObject.getJSONObject("globalSetting");
			if(globalSetting != null) {
				
//	Start Template Name validation
				isValid = globalSetting.optString("formName").equals("") ? false : true;
				if(!isValid) {
					httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
					response.put("message", MessageConstants.FORM_NAME_NOT_FOUND);
					response.put("status", HttpStatus.BAD_REQUEST.value());
					return isValid;
				}
//	End Template Name validation
				JSONArray optJSONArray = jsonFormTemplateObject.optJSONArray("formElements");
				if(optJSONArray == null || optJSONArray.length() < 1) {
					httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
					response.put("message", MessageConstants.INVALID_FORM_FEILDS_DATA);
					response.put("status", HttpStatus.BAD_REQUEST.value());
					return false;
				}
//	Start Application submission Date and Time Validation 
				startDateTime = globalSetting.optString("startDateTime");
				lastDateTime = globalSetting.optString("lastDateFromAccepting");
				if(!startDateTime.equals("") && !lastDateTime.equals("")) {
					isValid = isLastDateAfterStartDate(startDateTime,lastDateTime,ApplicationConstants.DEFAULT_DATE_TIME_FORMAT);				
					if(!isValid) {
						httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
						response.put("message", MessageConstants.INVALID_DATA_SUBMISSION_TIME);
						response.put("status", HttpStatus.BAD_REQUEST.value());
						return isValid;					
					}
				}
//	End Application submission Date and Time Validation 

// Start Limit Validation
				Boolean limit = globalSetting.optBoolean("limit");
				if(limit) {
					String submissionUnit = globalSetting.optString("submissionUnit");
					if(!ApplicationConstants.SUBMISSION_LIMIT_UNIT.contains(submissionUnit)) {
						isValid = false;
						response.put("message", MessageConstants.INVALID_SUBMISSION_LIMIT_UNIT);
						response.put("status", HttpStatus.BAD_REQUEST.value());
						return isValid;	
					}
				}
// End Limit Validation	
				
				if(!globalSetting.has("isGlobal")) {
					isValid = false;
					response.put("message", "isGlobal is not present");
					response.put("status", HttpStatus.BAD_REQUEST.value());
					return isValid;	
				}
			}else {
				isValid = false;
			}
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			isValid = false;
		} catch (JSONException e) {
			e.printStackTrace();
			isValid = false;
		}
		return isValid;
	}
	
}
