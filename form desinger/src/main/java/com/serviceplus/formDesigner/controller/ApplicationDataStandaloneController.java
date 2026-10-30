package com.serviceplus.formDesigner.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.request.model.ApplicationDataStandalone;
import com.serviceplus.formDesigner.service.ApplicationDataStandaloneService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ApplicationDataStandaloneController {
	
	private static final Logger LOGGER = LogManager.getLogger(FormSubscriptionController.class);
	
	@Autowired private ApplicationDataStandaloneService standaloneService;
	
	@GetMapping("/o/standalone-form/render")
	public ResponseEntity<?> renderStandaloneForm(@RequestParam String  tinyUrlToken, HttpServletRequest request) {
		try {
			return standaloneService.renderApplicationTemplateStandaloneForApply(tinyUrlToken, request);
		}catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Error : renderApplicationTemplateStandaloneForApply : {}",e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@PostMapping("/o/standalone-form/apply")
	public ResponseEntity<?> applyStandaloneForm(@RequestBody ApplicationDataStandalone applicationDataStandalone, HttpServletRequest request) {
		try {
			return standaloneService.applyApplicationTemplateStandaloneForApply(applicationDataStandalone);
		}catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Error : applyStandaloneForm : {}",e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}


}
