package com.serviceplus.metadata.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.RecordPendingApplicationRequestDTO;
import com.serviceplus.metadata.dto.SubmissionValidationRequestDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.ApplyService;
import com.serviceplus.metadata.service.SubmissionControlService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/apply")
public class ServiceApplyController {

	private final ApplyService applyService;
	private final SubmissionControlService submissionControlService;

	public ServiceApplyController(ApplyService applyService,
								  SubmissionControlService submissionControlService) {
		this.applyService = applyService;
		this.submissionControlService = submissionControlService;
	}

	@PostMapping("/serviceMetaData")
	public ResponseEntity<?> serviceMetaData(@RequestParam Integer serviceId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return applyService.serviceMetaData(serviceId, userSessionDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/resolveForm")
	public ResponseEntity<?> resolveForm(@RequestParam Integer baseServiceId,
			@RequestParam(required = false) Integer serviceId, @RequestParam(required = false) String taskId,
			HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return applyService.resolveForm(baseServiceId, serviceId, taskId, userSessionDetails);
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/processFlow")
	public ResponseEntity<?> processFlow(@RequestParam Integer serviceId, @RequestParam(required = false) String taskId,
			HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return applyService.processFlow(serviceId, taskId, userSessionDetails);
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/allowedAction")
	public ResponseEntity<?> applyAllowedAction(@RequestParam Integer serviceId, @RequestParam String taskId,
			HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return applyService.applyAllowAction(serviceId, taskId, userSessionDetails);
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/mvelDetails")
	public ResponseEntity<?> mvelDetails(@RequestParam Integer serviceId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			return applyService.mvelDetails(serviceId, userSessionDetails);
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/usedAttributeList")
	public ResponseEntity<?> getUsedAttr(@RequestParam Integer serviceId, @RequestParam String module,
			HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			return applyService.getUsedAttributeSet(serviceId, module, userSessionDetails);
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/validate-submission-location")
	public ResponseEntity<?> validateSubmissionLocation(@Valid @RequestBody SubmissionValidationRequestDTO requestDTO,
														HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return submissionControlService.evaluateSubmissionDecision(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/record-pending-adoption")
	public ResponseEntity<?> recordPendingAdoption(@Valid @RequestBody RecordPendingApplicationRequestDTO requestDTO,
												  HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return submissionControlService.recordPendingAdoptionApplication(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}