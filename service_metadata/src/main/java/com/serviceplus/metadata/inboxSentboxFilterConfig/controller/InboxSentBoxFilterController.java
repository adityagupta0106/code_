package com.serviceplus.metadata.inboxSentboxFilterConfig.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.inboxSentboxFilterConfig.dto.InboxSentBoxFilterDTO;
import com.serviceplus.metadata.inboxSentboxFilterConfig.service.InboxSentBoxFilterServiceImpl;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class InboxSentBoxFilterController {

	private static final Logger LOGGER = LogManager.getLogger(InboxSentBoxFilterController.class);
	private final InboxSentBoxFilterServiceImpl inboxSentBoxFilterService;

	public InboxSentBoxFilterController(InboxSentBoxFilterServiceImpl inboxSentBoxFilterService) {
		this.inboxSentBoxFilterService = inboxSentBoxFilterService;
	}

	@GetMapping("/inbox-sentbox-filter-config/all")
	public ResponseEntity<?> getAllConfigurations(
	        @RequestParam(required = false, defaultValue = "0") Integer page,
	        @RequestParam(required = false, defaultValue = "10") Integer size,
	        HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return inboxSentBoxFilterService.getAllConfigurations(page, size, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Controller : Error while fetching all inbox/sent box filter configurations", ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/inbox-sentbox-filter-config")
	public ResponseEntity<?> getConfiguration(@RequestParam Long id, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return inboxSentBoxFilterService.getConfiguration(id, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Controller : Error while fetching inbox/sent box filter configuration for id: {}", id, ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/inbox-sentbox-filter-config")
	public ResponseEntity<?> upsertConfiguration(@Valid @RequestBody InboxSentBoxFilterDTO inboxSentBoxFilterDTO, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return inboxSentBoxFilterService.upsertConfiguration(inboxSentBoxFilterDTO,userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Controller : Error while saving/updating inbox/sent box filter configuration", ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/inbox-sentbox-filter-config/status")
	public ResponseEntity<?> changeStatus(@RequestParam Long id, @RequestParam Boolean isActive, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return inboxSentBoxFilterService.changeStatus(id, isActive, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Controller : Error while changing status of inbox/sent box filter configuration for id: {}", id, ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/inbox-sentbox-filter-config/delete")
	public ResponseEntity<?> deleteConfiguration(@RequestParam Long id, HttpServletRequest request) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return inboxSentBoxFilterService.deleteConfiguration(id, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Controller : Error while deleting inbox/sent box filter configuration for id: {}", id, ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
