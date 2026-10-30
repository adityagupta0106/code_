package com.serviceplus.formDesigner.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.request.model.IApplicationTemplate;
import com.serviceplus.formDesigner.service.FormSubscriptionService;
import com.serviceplus.formDesigner.util.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/form")
public class FormSubscriptionController {

	private static final Logger FORM_SUBSCRIPTION = LogManager.getLogger(FormSubscriptionController.class);

	@Autowired private FormSubscriptionService formSubscriptionService;

	@GetMapping("/dept/all-published")
	public ResponseEntity<?> getDeptAllPublishForms( HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return formSubscriptionService.getDeptAllPublishForms(userSessionDetails);
		}catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : get all published form of department : {}",e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("/all-published")
	public ResponseEntity<?> getAllPublishForms(@RequestParam String mode, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return formSubscriptionService.getAllPublishForms(mode, userSessionDetails);
		}catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : get all published form of department : {}",e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/publish")
	public ResponseEntity<?> formPublish(@RequestParam String formId, HttpServletRequest request){
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return formSubscriptionService.formPublish(formId, userSessionDetails);
		}catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : publish form : {}",e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/un-publish")
	public ResponseEntity<?> formUnpublish(@RequestParam String formId, HttpServletRequest request) {
	    try {
	        UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
	        if (userSessionDetails == null) {
	            throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
	        }
	        IApplicationTemplate applicationTemplate = formSubscriptionService.formUnpublish(formId, userSessionDetails);

	        Map<String, Object> resObj = new HashMap<>();
	        resObj.put("formId", applicationTemplate.getFormId());
	        resObj.put("holderId", applicationTemplate.getHolderId());
	        resObj.put("formJsonData", applicationTemplate.getFormJsonData());
	        resObj.put("status", applicationTemplate.getStatus());

	        return ResponseEntity.ok(resObj);
	    } catch (Exception e) {
	        if (e instanceof SPRuntimeError) {
	            throw e;
	        }
	        FORM_SUBSCRIPTION.error("Error : unpublish form : {}", e);
	        throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@PostMapping("/subscribe")
	public ResponseEntity<?> formSubscribe(@RequestParam String formId) {
		try {
			return formSubscriptionService.formSubscribe(formId);
		}catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : subscribe form : {}",e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/un-subscribe")
	public ResponseEntity<?> formUnSubscribe(@RequestParam String formId, @RequestParam(required = false) Boolean makeCopy) {
		try {
			return formSubscriptionService.formUnsubscribe(formId, makeCopy);
		}catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : un-subscribe form version : {}",e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

}

