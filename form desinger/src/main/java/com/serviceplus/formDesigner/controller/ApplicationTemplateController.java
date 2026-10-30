package com.serviceplus.formDesigner.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.dto.FilterFormAttributeRequest;
import com.serviceplus.formDesigner.dto.FormAttributeRequest;
import com.serviceplus.formDesigner.dto.SpecificAttributesReqDTO;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.service.ApplicationTemplateService;
import com.serviceplus.formDesigner.util.CommonUtil;
import com.serviceplus.formDesigner.util.MessageConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class ApplicationTemplateController {

	@Autowired
	private ApplicationTemplateService applicationTemplateService;
	
	private static final Logger LOGGER = LogManager.getLogger(ApplicationTemplateController.class);

	@Value("${max.form.data}")
	private Long maxFormData;
	
	@PostMapping("/filter-attributes")
	public ResponseEntity<?> getFilterAttributes(@RequestBody FilterFormAttributeRequest formAttributeRequest) {
		try {
			return applicationTemplateService.getFilterAttributes(formAttributeRequest);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				LOGGER.error("Get all Filter Attributes : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("/form-data-view")
	@Validated
	public ResponseEntity<?> getApplications(
	        @RequestParam String holderId,
	        @RequestParam(required = false) String formId,
	        @RequestParam(defaultValue = "0") Integer page,
	        @RequestParam(defaultValue = "20") Integer size,
	        @RequestParam String mode,
	        HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			if(size > maxFormData) {
				throw new SPRuntimeError("Size can't be greater then "+maxFormData.toString(), HttpStatus.BAD_REQUEST);
			}
			if(mode.equalsIgnoreCase("service")) {
				return applicationTemplateService.getApplications(holderId, formId, page, size);
			}else if(mode.equalsIgnoreCase("standalone")) {
				return applicationTemplateService.getApplicationsForStandaloneForm(holderId, formId, page, size);
			}
			throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				LOGGER.error("Get all applications of forms : {}",ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/getAllFormData")
	public Object getAllIdAndName( HttpServletResponse httpServletResponse) {
		return applicationTemplateService.getFormIdAndFormName(httpServletResponse);
	}
	
	@PostMapping("/addFormData")
	public Map<String, Object> addFormData(@RequestBody ApplicationTemplate applicationTemplate,
			HttpServletResponse response, HttpServletRequest request) {
		try {
			return applicationTemplateService.addFormData(applicationTemplate, response, request);
		}catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("addFormData controller error", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}

	@PostMapping("/editFormData")
	public Map<String, Object> editFormData(@RequestBody ApplicationTemplate applicationTemplate,HttpServletRequest request,HttpServletResponse servletResponse) {
		
		try {
	        return applicationTemplateService.editFormData(applicationTemplate, request, servletResponse);
	    } catch (SPRuntimeError e) {
	        throw e;
	    } catch (Exception e) {
	    	LOGGER.error("editFormData controller error", e);
	        throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@PostMapping("/deleteByFormId")
	public Map<String, Object> deleteById(@RequestBody ApplicationTemplate applicationTemplate,
			HttpServletRequest request, HttpServletResponse response) {
		if (applicationTemplate.getFormId() == null || applicationTemplate.getFormId().isEmpty()) {
			throw new SPRuntimeError(MessageConstants.GIVE_FORM_ID, HttpStatus.BAD_REQUEST);
		}
		try {
			return applicationTemplateService.deleteById(applicationTemplate.getFormId(), request, response);
		} catch (SPRuntimeError e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("deleteById controller error", e);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    @GetMapping("/getByFormIdApplicant")
    public ResponseEntity<?> getByFormIdApplicant(@RequestParam String formId,@RequestParam( required = false) String applId, @RequestParam( required = false) Integer serviceId, @RequestParam( required = false) String taskId,@RequestParam( required = false) String txnId,HttpServletRequest request) {
        try{
        	return applicationTemplateService.getByIdApplicant(applId, formId,serviceId, taskId,txnId,request);
		} catch (SPRuntimeError e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	@GetMapping("/getByFormId")
	public ResponseEntity<?> getById(@RequestParam String formId,@RequestParam( required = false) String applId, @RequestParam( required = false) Integer serviceId, @RequestParam( required = false) String taskId,@RequestParam( required = false) String txnId,HttpServletRequest request) {
		return applicationTemplateService.getById(applId, formId,serviceId, taskId,txnId,request);
	}
	
	@GetMapping("/getAllDeletedForm")
	public Map<String,Object> getAllDeletedForm(HttpServletRequest request,HttpServletResponse httpServletResponse) {
		try {
			return applicationTemplateService.getAllDeletedForm(request,httpServletResponse);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@GetMapping("/restoreDeletedForm")
	public Map<String, Object> restoreDeletedForm(@RequestParam String formId,HttpServletRequest request, HttpServletResponse response){
		try {
			return applicationTemplateService.restoreDeletedForm(formId,response,request);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/getAllAttributesByFormId" , produces = "application/json")
	public ResponseEntity<?> getAllAttributesByFormId(@RequestParam String formId) {
		return applicationTemplateService.getAllAttributesByFormId(formId);
	}
	
	@PostMapping(value = "/all-forms/attributes")
	public ResponseEntity<?> getAllAttributesOfForms(@RequestBody FormAttributeRequest formAttributeRequest) {
		return applicationTemplateService.getAllAttributesOfForms(formAttributeRequest);
	}
	
	@GetMapping(value = "/forms/attributes")
	public ResponseEntity<?> getAllFormsAttributes(HttpServletRequest httpRequest) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
			}
			return applicationTemplateService.getAllFormsAttributesByUserId(userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("/attribute-for-link")
	public ResponseEntity<?> getAttributeForLink( @RequestParam String formId, HttpServletRequest request) {
	    UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
	    if (userSessionDetails == null) {
	        throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
	    }
	    return applicationTemplateService.getAttributeForLink(formId, userSessionDetails);
	}

    @PostMapping("/specific-attributes")
    public ResponseEntity<?> getSpecificAttributes(@Valid @RequestBody SpecificAttributesReqDTO specificAttributesReqDTO) {
        try {
            return applicationTemplateService.getSpecificAttributes(specificAttributesReqDTO);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
		 
}
