package com.serviceplus.metadata.correlationConfig.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.correlationConfig.dto.ServiceCorrelationRequestDTO;
import com.serviceplus.metadata.correlationConfig.service.ServiceCorrelationService;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ServiceCorrelationController {

	private static final Logger LOGGER = LogManager.getLogger(ServiceCorrelationController.class);

	private final ServiceCorrelationService serviceCorrelationService;

	public ServiceCorrelationController(ServiceCorrelationService serviceCorrelationService) {
		super();
		this.serviceCorrelationService = serviceCorrelationService;
	}

	@PostMapping("/service-correlation")
	public ResponseEntity<?> saveOrUpdate(@Valid @RequestBody ServiceCorrelationRequestDTO requestDTO, HttpServletRequest request) {

		try {
			LOGGER.info("Controller : Request received to save/update service correlation configuration");

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				LOGGER.warn("Controller : User session details not found while saving/updating service correlation configuration");
				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}
			return serviceCorrelationService.save(requestDTO, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Controller : Error while saving/updating service correlation configuration", ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/service-correlation/delete")
	public ResponseEntity<?> delete(@RequestParam Integer id, HttpServletRequest request) {

		try {

			LOGGER.info("Controller : Request received to delete service correlation configuration, id: {}", id);
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				LOGGER.warn("Controller : User session details not found while deleting service correlation configuration, id: {}",id);
				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}
			return serviceCorrelationService.delete(id, userSessionDetails);
		} catch (Exception ex) {

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Controller : Error while deleting service correlation configuration, id: {}", id, ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/service-correlation/all")
	public ResponseEntity<?> getAll(
			@RequestParam Integer serviceId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "100") int size,
	        HttpServletRequest request) {
		try {

			LOGGER.info("Controller : Request received to fetch all service correlation configurations");
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				LOGGER.warn("Controller : User session details not found while fetching all service correlation configurations");
				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			return serviceCorrelationService.getAll(userSessionDetails, serviceId, page, size);
		} catch (Exception ex) {

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			LOGGER.error("Controller : Error while fetching all service correlation configurations", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/service-correlation/dep/all")
	public ResponseEntity<?> getAllBasedOnClcAndTenant(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "100") int size,
			HttpServletRequest request) {
		try {
			
			LOGGER.info("Controller : Request received to fetch all service correlation configurations based on Clc and Tenant");
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				LOGGER.warn("Controller : User session details not found while fetching all service correlation configurations based on Clc and Tenant");
				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}
			
			return serviceCorrelationService.getAllBasedOnClcAndTenant(userSessionDetails, page, size);
		} catch (Exception ex) {
			
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			
			LOGGER.error("Controller : Error while fetching all service correlation configurations based on Clc and Tenant", ex);
			
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/service-correlation")
	public ResponseEntity<?> getById(@RequestParam Integer id, HttpServletRequest request) {

		try {

			LOGGER.info("Controller : Request received to fetch service correlation configuration, id: {}", id);

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				LOGGER.warn("Controller : User session details not found while fetching service correlation configuration, id: {}", id);
				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			return serviceCorrelationService.getById(id, userSessionDetails);

		} catch (Exception ex) {

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			LOGGER.error("Controller : Error while fetching service correlation configuration, id: {}", id, ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}