package com.serviceplus.formDesigner.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.request.model.ApplicationTemplateStandalone;
import com.serviceplus.formDesigner.service.ApplicationTemplateStandaloneService;
import com.serviceplus.formDesigner.util.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ApplicationTemplateStandaloneController {
	private static final Logger LOGGER = LogManager.getLogger(ApplicationTemplateStandaloneController.class);
	private final ApplicationTemplateStandaloneService applicationTemplateStandaloneService;

	public ApplicationTemplateStandaloneController(
			ApplicationTemplateStandaloneService applicationTemplateStandaloneService) {
		super();
		this.applicationTemplateStandaloneService = applicationTemplateStandaloneService;
	}

	@Value("${max.form.data}")
	private Long maxFormData;

	@PostMapping("/standalone-form")
	public ResponseEntity<?> upsert(@RequestBody ApplicationTemplateStandalone applicationTemplateStandalone,
			HttpServletRequest request, HttpServletResponse httpServletResponse) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}

			return applicationTemplateStandaloneService.upsert(applicationTemplateStandalone, userSessionDetails, httpServletResponse);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				LOGGER.error("Controller : Save standalone form details : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/standalone-form")
	public ResponseEntity<?> getStandaloneFormDetails(@RequestParam String formId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.sendDataForRender(formId, request);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				LOGGER.error("Controller : Get standalone form details : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/standalone-form/all")
	public ResponseEntity<?> getAllStandaloneFormDetails(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}

			return applicationTemplateStandaloneService.getFormIdAndFormName();
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				LOGGER.error("Controller : Get all standalone form details : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/publish-as-standalone")
	public ResponseEntity<?> formPublishAsStandalone(@RequestParam String formId,
			@RequestParam(required = false, defaultValue = "true") Boolean isOnline, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.formPublish(formId, isOnline, userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Controller : publish form as standalone: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@PostMapping("/standalone-form/online")
	public ResponseEntity<?> online(@RequestParam String formId,@RequestParam Boolean isOnline, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.online(formId, isOnline, userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Controller : online/offline for production : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/standalone-form/archive")
	public ResponseEntity<?> archiveForm(@RequestParam String formId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.archiveForm(formId, userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Controller : archive standalone form: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/standalone-form/trash")
	public ResponseEntity<?> trashForm(@RequestParam String formId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.trashForm(formId, userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Controller : trash standalone form: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/standalone-form/delete")
	public ResponseEntity<?> deleteForm(@RequestParam String formId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.delete(formId, userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Controller : delete standalone form: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@PostMapping("/standalone-form/restore")
	public ResponseEntity<?> restore(@RequestParam String formId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.restore(formId, userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Controller : restore standalone form: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/standalone-form/archive/all")
	public ResponseEntity<?> getAllArchiveForm(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.getAllArchiveForm(userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Controller : Get all archive standalone forms: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/standalone-form/trash/all")
	public ResponseEntity<?> getAllTrashForm(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateStandaloneService.getAllTrashForm(userSessionDetails);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Controller : Get all trash standalone forms: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

//	@GetMapping("/form-data-view")
//	@Validated
//	public ResponseEntity<?> getApplications(
//	        @RequestParam String holderId,
//	        @RequestParam(required = false) String formId,
//	        @RequestParam(defaultValue = "0") Integer page,
//	        @RequestParam(defaultValue = "20") Integer size,
//	        @RequestParam String mode,
//	        HttpServletRequest request) {
//		try {
//			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
//			if (userSessionDetails == null) {
//				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
//			}
//			if(size > maxFormData) {
//				throw new SPRuntimeError("Size can't be greater then "+maxFormData.toString(), HttpStatus.BAD_REQUEST);
//			}
	//// return
	/// applicationTemplateService.getApplicationsForStandaloneForm(holderId, formId,
	/// page, size);
//			return null;
//		} catch (Exception ex) {
//			if (ex instanceof SPRuntimeError) {
//				throw ex;
//			} else {
//				ex.printStackTrace();
//				LOGGER.error("Get all applications of forms : {}",ex);
//				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//	}

}
