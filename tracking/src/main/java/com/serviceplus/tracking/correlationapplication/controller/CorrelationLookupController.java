package com.serviceplus.tracking.correlationapplication.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.correlationapplication.dto.CorrelationLookupRequest;
import com.serviceplus.tracking.correlationapplication.dto.CorrelationLookupResponse;
import com.serviceplus.tracking.correlationapplication.service.CorrelationLookupService;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/a/correlation")
public class CorrelationLookupController {

	private static final Logger LOGGER = LogManager.getLogger(CorrelationLookupController.class);

	private final CorrelationLookupService lookupService;

	public CorrelationLookupController(CorrelationLookupService lookupService) {
		this.lookupService = lookupService;
	}

	@PostMapping("/lookup")
	public ResponseEntity<CorrelationLookupResponse> lookup(@RequestBody CorrelationLookupRequest request,
			HttpServletRequest httprequest) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httprequest);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized", HttpStatus.UNAUTHORIZED);
			}
			
			if (request == null || request.getServiceId() == null || request.getTaskId() == null
					|| request.getTaskId().trim().isEmpty()) {
				return ResponseEntity.badRequest().build();
			}

			LOGGER.info("Correlation lookup request. serviceId={}, taskId={}", request.getServiceId(),
					request.getTaskId());

			return ResponseEntity.ok(lookupService.lookup(request));

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOGGER.error("Error get Filter Attributes", ex);
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
}