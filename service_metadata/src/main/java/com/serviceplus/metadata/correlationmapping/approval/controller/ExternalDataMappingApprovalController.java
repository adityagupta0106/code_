package com.serviceplus.metadata.correlationmapping.approval.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.correlationConfig.approval.dto.ApprovalDetailsRequestDTO;
import com.serviceplus.metadata.correlationConfig.approval.dto.ExternalDataMappingApprovalRequestDTO;
import com.serviceplus.metadata.correlationmapping.approval.service.ExternalDataMappingApprovalService;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ExternalDataMappingApprovalController {
	private static final Logger LOGGER = LogManager.getLogger(ExternalDataMappingApprovalController.class);

	private final ExternalDataMappingApprovalService dataMappingApprovalService;

	public ExternalDataMappingApprovalController(ExternalDataMappingApprovalService dataMappingApprovalService) {
		super();
		this.dataMappingApprovalService = dataMappingApprovalService;
	}

	@GetMapping("/dataset/approval")
	public ResponseEntity<?> getDataSetForApproval(HttpServletRequest request) {

		LOGGER.info("Controller : Get Data Set For Approval - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				LOGGER.warn("Controller : Get Data Set For Approval - Unauthorized access attempt");

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			LOGGER.debug("Controller : Get Data Set For Approval - userId={}, tenantId={}, entityId={}",
					userSessionDetails.getUserID(), userSessionDetails.getTenantId(), userSessionDetails.getEntityId());

			return dataMappingApprovalService.getDataSetForApproval(userSessionDetails);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Controller : Get Data Set For Approval - Exception occurred", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/dataset/approval/details")
	public ResponseEntity<?> getApprovalDetails(@RequestBody @Valid ApprovalDetailsRequestDTO requestDTO,
			HttpServletRequest request) {

		LOGGER.info("Controller : Get Approval Details - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				LOGGER.warn("Controller : Get Approval Details - Unauthorized access attempt");

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			LOGGER.debug("Controller : Get Approval Details - userId={}, tenantId={}, entityId={}",
					userSessionDetails.getUserID(), userSessionDetails.getTenantId(), userSessionDetails.getEntityId());

			return dataMappingApprovalService.getApprovalDetails(requestDTO, userSessionDetails);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Controller : Get Approval Details - Exception occurred", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/dataset/approval")
	public ResponseEntity<?> approveDataSet(@RequestBody @Valid ExternalDataMappingApprovalRequestDTO requestDTO,
			HttpServletRequest request) {

		LOGGER.info("Controller : Dataset Approval - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				LOGGER.warn("Controller : Dataset Approval - Unauthorized access");
				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			return dataMappingApprovalService.approveDataSet(requestDTO, userSessionDetails);

		} finally {
			LOGGER.info("Controller : Dataset Approval - END");
		}
	}
}
