package com.serviceplus.metadata.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.MoveToProductionRequestDTO;
import com.serviceplus.metadata.dto.ServiceLaunchDocumentResponseDTO;
import com.serviceplus.metadata.dto.ServiceLaunchPendingDTO;
import com.serviceplus.metadata.dto.ServiceLaunchRequestDTO;
import com.serviceplus.metadata.dto.ServiceLaunchResponseDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.ServiceLaunchService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ServiceLaunchController {

	private final ServiceLaunchService serviceLaunchService;

	public ServiceLaunchController(ServiceLaunchService serviceLaunchService) {
		this.serviceLaunchService = serviceLaunchService;
	}

	@PostMapping("/launch-service")
	public ResponseEntity<ServiceLaunchResponseDTO> launchService(@RequestBody ServiceLaunchRequestDTO request,
			HttpServletRequest servletRequest) {
		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(servletRequest);
		if (userSessionDetails == null) {
			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
		}
		return ResponseEntity.ok(serviceLaunchService.launchService(request, userSessionDetails));
	}

	@GetMapping("/pending-for-approval")
	public ResponseEntity<List<ServiceLaunchPendingDTO>> getPendingLaunches(HttpServletRequest servletRequest) {
		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(servletRequest);
		if (userSessionDetails == null) {
			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
		}
		return ResponseEntity.ok(serviceLaunchService.getPendingLaunches(userSessionDetails));
	}
	
	@GetMapping("/launch-document")
	public ResponseEntity<ServiceLaunchDocumentResponseDTO> getLaunchDocument(@RequestParam Integer serviceId,
			HttpServletRequest servletRequest) {

		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(servletRequest);
		if (userSessionDetails == null) {
			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
		}
		return ResponseEntity.ok(serviceLaunchService.getLaunchDocument(serviceId, userSessionDetails));
	}

	@PostMapping("/move-to-production")
	public ResponseEntity<?> moveToProduction(@RequestBody MoveToProductionRequestDTO request,HttpServletRequest servletRequest) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(servletRequest);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return serviceLaunchService.moveToProduction(request, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/reject-launch")
	public ResponseEntity<?> rejectLaunch(@RequestParam Integer serviceId, HttpServletRequest servletRequest) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(servletRequest);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return serviceLaunchService.rejectLaunch(serviceId, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
