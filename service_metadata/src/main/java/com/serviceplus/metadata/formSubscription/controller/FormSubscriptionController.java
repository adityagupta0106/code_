package com.serviceplus.metadata.formSubscription.controller;

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

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.formSubscription.dto.ManualFormSaveReqDTO;
import com.serviceplus.metadata.formSubscription.service.FormSubscriptionService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class FormSubscriptionController {
	private static final Logger FORM_SUBSCRIPTION = LogManager.getLogger("formSubscription");

	@Autowired
	private FormSubscriptionService service;

	@GetMapping("/manual-subscription-tasks")
	public ResponseEntity<?> getAllTasks(@RequestParam Integer serviceId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			return service.getAllTasks(serviceId, userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : Get all task : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/manual-subscription-service")
	public ResponseEntity<?> getAllService(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			return service.getAllService(userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : Get all service : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/manual-subscription-save")
	public ResponseEntity<?> saveManual(@Valid @RequestBody ManualFormSaveReqDTO dto, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return service.saveManual(dto, userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : save manual form subscription action : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}
}
