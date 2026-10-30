package com.serviceplus.metadata.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.ServiceOutputFormatService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/serdoc")
public class ServiceOutputFormatController {
	private static final Logger LOGGER = LogManager.getLogger(ServiceOutputFormatController.class);
	@Autowired
	private ServiceOutputFormatService outputFormatService;

	@GetMapping("/all-certificate")
	public ResponseEntity<?> allCertificate(@RequestParam Integer serviceId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
			if (tokenValidationResult != null) {
				return tokenValidationResult;
			}
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return outputFormatService.allCertificate(serviceId);

		} catch (Exception ex) {
			LOGGER.error("Get all certificate for service id {}, {}", serviceId, ex);
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/all-notif")
	public ResponseEntity<?> allNotification(@RequestParam Integer serviceId,
			@RequestParam(required = false) String type, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
			if (tokenValidationResult != null) {
				return tokenValidationResult;
			}
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return outputFormatService.allNotification(serviceId, type);

		} catch (Exception ex) {
			LOGGER.error("Get all notification for service id {}, {}", serviceId, ex);
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/pre-task")
	public ResponseEntity<?> getPreviousTaskDoc(@RequestParam Integer serviceId,
			@RequestParam(required = false) String taskId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
			if (tokenValidationResult != null) {
				return tokenValidationResult;
			}
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return outputFormatService.getPreviousTaskDoc(serviceId, taskId, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOGGER.error("Get all previous for service id {},taskId {}, {}", serviceId, taskId, ex);
				responseJson.put("errorCode", "500");
				responseJson.put("errorMessage", "Internal Server Error");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}

}
