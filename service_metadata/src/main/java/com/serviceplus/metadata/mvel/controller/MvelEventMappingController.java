package com.serviceplus.metadata.mvel.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.mvel.dto.CompileResponse;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO;
import com.serviceplus.metadata.mvel.service.MvelEventMappingService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/mvel/event-mappings")
public class MvelEventMappingController {

	@Autowired
	private MvelEventMappingService mvelEventMappingService;

	private static final Logger logger = LoggerFactory.getLogger(MvelEventMappingController.class);

	@PostMapping("/save")
	public ResponseEntity<?> save(HttpServletRequest httpRequest,
			@Valid @RequestBody MvelEventMappingDTO mvelEventMappingDTO) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				return unauthorizedUserResp(responseJson);
			}

			return mvelEventMappingService.save(userSessionDetails.getUserID(), mvelEventMappingDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Unexpected error: {}", ex.getMessage());
			return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));

		}
	}

	@GetMapping("/get-all")
	public ResponseEntity<?> getAll(HttpServletRequest httpRequest, @RequestParam Integer serviceId) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				return unauthorizedUserResp(responseJson);
			}

			return mvelEventMappingService.getAll(userSessionDetails.getUserID(), serviceId);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Unexpected error: {}", ex.getMessage());
			return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));

		}
	}

	@GetMapping("/delete")
	public ResponseEntity<?> getAll(HttpServletRequest httpRequest, @RequestParam Integer serviceId,
			@RequestParam Long eventId) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				return unauthorizedUserResp(responseJson);
			}

			return mvelEventMappingService.delete(userSessionDetails.getUserID(), serviceId, eventId);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Unexpected error: {}", ex.getMessage());
			return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));

		}
	}

	private ResponseEntity<?> unauthorizedUserResp(JSONObject responseJson) {
		responseJson.put("errorCode", "401");
		responseJson.put("errorMessage", "Unauthorized user");
		return new ResponseEntity<>(responseJson.toString(), HttpStatus.UNAUTHORIZED);
	}

}
