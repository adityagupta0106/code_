package com.serviceplus.metadata.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.serviceplus.metadata.dto.ProcessFlowDTO;
import com.serviceplus.metadata.dto.ProcessFlowSubmissionDetailsDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.ProcessFlowService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ProcessFlowController {

	@Autowired
    private ProcessFlowService processFlowService;

	@PostMapping("/saveProcessFlow")
    public ResponseEntity<?> saveProcessFlow(@RequestBody ProcessFlowDTO processFlowDTO,
                                             HttpServletRequest request) {
    	
    	JSONObject responseJson= new JSONObject();
    	try {
    	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    	//userSessionDetails=getLocalSessionDetails(userSessionDetails);
    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, processFlowDTO.getServiceId(), responseJson);
        if (tokenValidationResult != null) {
            return tokenValidationResult;
        }
		if (userSessionDetails == null) {
        	responseJson.put("errorCode", "400");
        	responseJson.put("errorMessage", "Bad Request");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}
		return processFlowService.saveProcessFlow(processFlowDTO, userSessionDetails);
	} catch (Exception ex) {
		ex.printStackTrace();
		if (ex instanceof SPRuntimeError) {
			throw ex;
		} else {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    }
	
	@PostMapping("/saveDraftProcessFlow")
    public ResponseEntity<?> saveDraftProcessFlow(@RequestBody JsonNode processFlowDTO,HttpServletRequest request) {
    	
    	JSONObject responseJson= new JSONObject();
    	try {
    	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    	userSessionDetails=getLocalSessionDetails(userSessionDetails);
//    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, processFlowDTO.getServiceId(), responseJson);
//        if (tokenValidationResult != null) {
//            return tokenValidationResult;
//        }
		if (userSessionDetails == null) {
        	responseJson.put("errorCode", "400");
        	responseJson.put("errorMessage", "Bad Request");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}
		return processFlowService.saveDraftProcessFlow(processFlowDTO,userSessionDetails);
    	}catch(Exception ex) {
    		responseJson.put("errorCode", "500");
        	responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
	
	@GetMapping("/processFlow")
    public ResponseEntity<?> getProcessFlow(@RequestParam Integer serviceId,
                                            HttpServletRequest request) {
    	
    	JSONObject responseJson= new JSONObject();
    	
    	try {
    	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    	userSessionDetails=getLocalSessionDetails(userSessionDetails);
    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
        if (tokenValidationResult != null) {
            return tokenValidationResult;
        }
		if (userSessionDetails == null) {
        	responseJson.put("errorCode", "400");
        	responseJson.put("errorMessage", "Bad Request");
            return new ResponseEntity<>(responseJson, HttpStatus.BAD_REQUEST);
		}
		return processFlowService.getProcessFlow(serviceId, userSessionDetails, "");
		
    	}catch(Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
    		responseJson.put("errorCode", "500");
        	responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
	
	@GetMapping("/draftProcessFlow")
    public ResponseEntity<?> draftProcessFlow(@RequestParam Integer serviceId,HttpServletRequest request) {
    	
    	JSONObject responseJson= new JSONObject();
    	
    	try {
    	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    	userSessionDetails=getLocalSessionDetails(userSessionDetails);
    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
        if (tokenValidationResult != null) {
            return tokenValidationResult;
        }
		if (userSessionDetails == null) {
        	responseJson.put("errorCode", "400");
        	responseJson.put("errorMessage", "Bad Request");
            return new ResponseEntity<>(responseJson, HttpStatus.BAD_REQUEST);
		}
		
		return processFlowService.getProcessFlow(serviceId,userSessionDetails,"D");
		
    	}catch(Exception ex) {
    		responseJson.put("errorCode", "500");
        	responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
	

	@GetMapping("/submission-mode-details")
	public ResponseEntity<?> getSubmissionNodeDetails(@RequestParam Integer serviceId, HttpServletRequest request) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			ProcessFlowSubmissionDetailsDTO result = processFlowService.getSubmissionNodeDetails(serviceId,
					userSessionDetails);

			if (result == null) {
				responseJson.put("errorCode", "404");
				responseJson.put("errorMessage", "Process flow node s_1 not found");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.NOT_FOUND);
			}

			return ResponseEntity.ok(result);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}

			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");

			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
}

	

	private UserSessionDTO getLocalSessionDetails(UserSessionDTO userSessionDetails) {
//		userSessionDetails = new UserSessionDTO();
//		userSessionDetails.setUserID(227175);
//		userSessionDetails.setUserID(-2L);
//		userSessionDetails.setClcId(32);
//		userSessionDetails.setEntityId(544);
//		userSessionDetails.setTenantId("68d29060b44b6f4239cd5abc");
		return userSessionDetails;
	}
}
