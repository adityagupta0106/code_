package com.serviceplus.formDesigner.service;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.formDesigner.controller.FormSubscriptionController;
import com.serviceplus.formDesigner.enums.FormStandaloneStatus;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.repository.ApplicationDataStandaloneRepository;
import com.serviceplus.formDesigner.repository.ApplicationTemplateStandaloneRepository;
import com.serviceplus.formDesigner.request.model.ApplicationDataStandalone;
import com.serviceplus.formDesigner.request.model.ApplicationTemplateStandalone;
import com.serviceplus.formDesigner.util.MessageConstants;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ApplicationDataStandaloneService {

	private static final Logger LOGGER = LogManager.getLogger(FormSubscriptionController.class);
	
	@Autowired private ApplicationTemplateStandaloneRepository standaloneRepository;
	@Autowired private ApplicationTemplateStandaloneService applicationTemplateStandaloneService;
	@Autowired private ApplicationDataStandaloneRepository applicationDataStandaloneRepository;
	
	
	public ResponseEntity<?> renderApplicationTemplateStandaloneForApply(String token, HttpServletRequest request) {
		try {
			ApplicationTemplateStandalone applicationTemplateStandalone = standaloneRepository.findByTinyUrlToken(token).orElseThrow(() -> new SPRuntimeError(MessageConstants.INVALID_FORM, HttpStatus.BAD_REQUEST));
			
			if(applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.ARCHIVED.getValue()) || applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.TRASHED.getValue())) {
				throw new SPRuntimeError("We're sorry. This Form is in ARCHIVED/TRASHED.", HttpStatus.BAD_REQUEST);
			}
			
// 			prod code
//			if(!applicationTemplateStandalone.getStatus().equals(FormStatus.PUBLISHED.getValue())) {
//				throw new SPRuntimeError("We're sorry. This Form is not published.", HttpStatus.BAD_REQUEST);
//			}
//			if(!applicationTemplateStandalone.getIsOnline()) {
//				throw new SPRuntimeError("We're sorry. This Form is not online.", HttpStatus.BAD_REQUEST);
//			}
			
			Long clickCount = applicationTemplateStandalone.getClickCount() + 1;
			applicationTemplateStandalone.setClickCount(clickCount);
			standaloneRepository.save(applicationTemplateStandalone);
			return applicationTemplateStandaloneService.sendDataForRender(applicationTemplateStandalone.getFormId(), request);
		} catch (SPRuntimeError e) {
	        throw e;
		} catch (Exception e) {
			LOGGER.error("Error : sendDataForRender", e);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	public ResponseEntity<?> applyApplicationTemplateStandaloneForApply(ApplicationDataStandalone applicationDataStandalone) {
		try {
			ApplicationTemplateStandalone applicationTemplateStandalone = standaloneRepository.findById(applicationDataStandalone.getFormId()).orElseThrow(() -> new SPRuntimeError(MessageConstants.INVALID_FORM_ID, HttpStatus.BAD_REQUEST));
			
			if(applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.ARCHIVED.getValue()) || applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.TRASHED.getValue())) {
				throw new SPRuntimeError("We're sorry. This Form is in ARCHIVED/TRASHED.", HttpStatus.BAD_REQUEST);
			}
			
// 			prod code
//			if(!applicationTemplateStandalone.getStatus().equals(FormStatus.PUBLISHED.getValue())) {
//				throw new SPRuntimeError("We're sorry. This Form is not published.", HttpStatus.BAD_REQUEST);
//			}
//			if(!applicationTemplateStandalone.getIsOnline()) {
//				throw new SPRuntimeError("We're sorry. This Form is not online.", HttpStatus.BAD_REQUEST);
//			}
			
			
			String holderId = applicationTemplateStandalone.getHolderId();
			applicationDataStandalone.setHolderId(holderId);
			
			applicationDataStandaloneRepository.save(applicationDataStandalone);
			Map<String, Object> finalResponse = new HashMap<String, Object>();
			applicationDataStandalone.setCreatedOn(new Date());
			applicationDataStandaloneRepository.save(applicationDataStandalone);
			finalResponse.put("message", MessageConstants.DATA_SAVE);

			return ResponseEntity.ok(finalResponse);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Error : applyStandaloneForm : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
}
