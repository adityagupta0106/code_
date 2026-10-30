package com.serviceplus.metadata.notification.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.notification.dto.SandesConfigDTOReqDTO;
import com.serviceplus.metadata.notification.service.SandesConfigurationService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sandes-cnf")
public class SandesConfigurationController {
	@Autowired
	private SandesConfigurationService service;

	private static final Logger NOTIF_TEMP_DESIGN = LogManager.getLogger("notifTempDesign");

	@GetMapping("/get-all")
	public ResponseEntity<?> getAllSMSProvider(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return service.getAllSandesProviderOfUser(userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Get all sandes-providers : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("/get")
	public ResponseEntity<?> getSMSProvider(HttpServletRequest request, @RequestParam Long id) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return service.getNotificationProvider(userSessionDetails, id);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Get sandes-providers : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("/get-all/dept")
	public ResponseEntity<?> getAllSMSProviderOfDeptWithStatus(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return service.getAllSandesProviderOfDeptWithStatus(userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Get all active sms-providers of department :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/status")
	public ResponseEntity<?> changeStatus(HttpServletRequest request, @RequestParam Long id) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return service.changeStatus(userSessionDetails, id);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Change Status : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/delete")
	public ResponseEntity<?> delete(HttpServletRequest request, @RequestParam Long id) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return service.delete(userSessionDetails, id);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Change Status : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@PostMapping("/upsert")
	public ResponseEntity<?> upsertSmsProvider(@Valid @RequestBody SandesConfigDTOReqDTO dto,
			HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return service.upsertSandesProvider(userSessionDetails, dto);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Insert/Update sandes-configuration : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}

}
