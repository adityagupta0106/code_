package com.serviceplus.metadata.pattern.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.pattern.dto.ServicePatternConfigurationDTO;
import com.serviceplus.metadata.pattern.dto.ServicePatternSummaryDTO;
import com.serviceplus.metadata.pattern.service.ServicePatternConfigurationService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ServicePatternConfigurationController {

	private static final Logger logger = LoggerFactory.getLogger(ServicePatternConfigurationController.class);

	private final ServicePatternConfigurationService servicePatternConfigurationService;

	public ServicePatternConfigurationController(
			ServicePatternConfigurationService servicePatternConfigurationService) {
		this.servicePatternConfigurationService = servicePatternConfigurationService;
	}

	@PostMapping("/service-pattern")
	public ResponseEntity<?> saveServicePattern(@Valid @RequestBody ServicePatternConfigurationDTO dto,
			HttpServletRequest request) {

		logger.info("ServicePatternConfigurationController: Save service pattern called. patternTemplateId={}",
				dto.getPatternId());

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			return servicePatternConfigurationService.saveOrUpdateServicePattern(dto, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("Error saving service pattern configuration : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/service-pattern")
	public ResponseEntity<ServicePatternConfigurationDTO> getServicePattern(@RequestParam(value = "id") Long id,
			HttpServletRequest request) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			logger.info("ServicePatternConfigurationController: Fetching service pattern. Id={}", id);
			ServicePatternConfigurationDTO response = servicePatternConfigurationService.getServicePattern(id);
			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("Error fetching service pattern configuration : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/service-pattern/all")
	public ResponseEntity<List<ServicePatternSummaryDTO>> getAllServicePatterns(
			@RequestParam(value = "serviceId") Long serviceId,
			HttpServletRequest request) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			logger.info("ServicePatternConfigurationController: Fetching all service patterns. serviceId={}",
					serviceId);
			List<ServicePatternSummaryDTO> response = servicePatternConfigurationService
					.getAllServicePatterns(serviceId, userSessionDetails);
			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("Error fetching all service patterns : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/service-pattern/delete")
	public ResponseEntity<?> deleteServicePattern(@RequestParam("id") Long id, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			logger.info("ServicePatternConfigurationController: Delete service pattern called for id: {}", id);
			return servicePatternConfigurationService.deleteServicePattern(id, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("Error deleting service pattern configuration : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

}
