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
import com.serviceplus.metadata.mvel.dto.CompileRequest;
import com.serviceplus.metadata.mvel.dto.CompileResponse;
import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO;
import com.serviceplus.metadata.mvel.service.MvelFunctionService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/mvel")
public class MvelFunctionController {
	@Autowired
	private MvelFunctionService mvelService;
	
	private static final Logger logger = LoggerFactory.getLogger(MvelFunctionController.class);
	
	@GetMapping("param-type")
	 public ResponseEntity<?> getparameterTypes(HttpServletRequest httpRequest){
		JSONObject responseJson= new JSONObject();
        try {
	        	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
				if (userSessionDetails == null) {
					return unauthorizedUserResp(responseJson);
				}
				boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
				if (!hasRole) {
					return unauthorizedUserResp(responseJson);
				}
				
				return mvelService.getMvelFunParameterTypes();
        	} catch (Exception ex) {
	        	ex.printStackTrace();
	            logger.error("Unexpected error: {}", ex.getMessage());
	            return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));
	            
	        }
	}
	
	@GetMapping("trigger-points")
	public ResponseEntity<?> getMvelTriggerPoints(HttpServletRequest httpRequest){
		JSONObject responseJson= new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				logger.error("Mvel Trigger Points error: {}", "Session user is null");
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				logger.error("Mvel Trigger Points error: {}", "Unauthorized user");
				return unauthorizedUserResp(responseJson);
			}
			
			return mvelService.getMvelTriggerPoints();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Unexpected error: {}", ex.getMessage());
			return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));
			
		}
	}
	
	@PostMapping("function-param")
	public ResponseEntity<?> getFunctionParam(HttpServletRequest httpRequest, @RequestParam Long id) {
		JSONObject responseJson = new JSONObject();
		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				logger.error("Compilation MVEL error: {}", "Session user is null");
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				logger.error("Compilation MVEL error: {}", "Unauthorized user");
				return unauthorizedUserResp(responseJson);
			}
			
			return mvelService.getFunctionParam(userSessionDetails,id); 
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Unexpected error: {}", ex.getMessage());
			return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));

		}
	}
	
	@PostMapping("/compile")
    public ResponseEntity<?> compileMvel(HttpServletRequest httpRequest,@Valid @RequestBody CompileRequest requestData) {
		logger.info("Compiling MVEL expression...");
		JSONObject responseJson= new JSONObject();
        try {
        	
        	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				logger.error("Compilation MVEL error: {}", "Session user is null");
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				logger.error("Compilation MVEL error: {}", "Unauthorized user");
				return unauthorizedUserResp(responseJson);
			}
			
            mvelService.compileExpression(requestData.getFnBody());
            return ResponseEntity.ok(new CompileResponse(true, "MVEL expression compiled successfully."));
            
        } catch (IllegalArgumentException ex) {
        	ex.printStackTrace();
            logger.error("Compilation MVEL error: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(new CompileResponse(false, ex.getMessage()));
            
        } catch (Exception ex) {
        	ex.printStackTrace();
            logger.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));
            
        }
    }
	
	@PostMapping("/save")
    public ResponseEntity<?> addMavelFunction(HttpServletRequest httpRequest,@Valid @RequestBody MvelFunctionDTO requestData) {
		logger.info("Add MVEL Function...");
		JSONObject responseJson= new JSONObject();
        try {
        	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				logger.error("Add MVEL  error: {}", "Session user is null");
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				logger.error("Add MVEL  error: {}", "Unauthorized user");
				return unauthorizedUserResp(responseJson);
			}
			 return mvelService.addMvelFunction(userSessionDetails,requestData);
        } catch (Exception ex) {
        	ex.printStackTrace();
            logger.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));
            
        }
    }
	
	@GetMapping("/get-all")
	public ResponseEntity<?> getAllListOfMavelFunction(HttpServletRequest httpRequest,@RequestParam Integer serviceId) {
		logger.info("Get all list of MVEL Function...");
		JSONObject responseJson= new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				logger.error("Get all list of MVEL Function error: {}", "Session user is null");
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				logger.error("Get all list of MVEL Function error: {}", "Unauthorized user");
				return unauthorizedUserResp(responseJson);
			}
			return mvelService.getAllListOfMavelFunction(userSessionDetails,serviceId);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Get all list of MVEL Function error: {}", ex.getMessage());
			return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));
			
		}
	}
	
	@GetMapping("/get")
	public ResponseEntity<?> getMavelFunction(HttpServletRequest httpRequest,
			@RequestParam @NotNull(message = "serviceId cannot be null") @Min(value = 1, message = "serviceId must be greater than 0") Integer serviceId,
			@RequestParam @NotNull(message = "id cannot be null") @Min(value = 1, message = "id must be greater than 0") Long id) {
		logger.info("Get MVEL Function...");
		JSONObject responseJson= new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				logger.error("Get MVEL Function error: {}", "Session user is null");
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				logger.error("Get of MVEL Function error: {}", "Unauthorized user");
				return unauthorizedUserResp(responseJson);
			}
			return mvelService.getMavelFunction(userSessionDetails,serviceId,id);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Get MVEL Function error: {}", ex.getMessage());
			return ResponseEntity.internalServerError().body(new CompileResponse(false, "Internal server error"));
			
		}
	}
	
	@PostMapping("/delete")
	public ResponseEntity<?> deleteMavelFunction(HttpServletRequest httpRequest,
			@RequestParam @NotNull(message = "serviceId cannot be null") @Min(value = 1, message = "serviceId must be greater than 0") Integer serviceId,
			@RequestParam @NotNull(message = "id cannot be null") @Min(value = 1, message = "id must be greater than 0") Long id) {

		logger.info("Delete MVEL Function...");
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				logger.error("Delete MVEL error: {}", "Session user is null");
				return unauthorizedUserResp(responseJson);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				logger.error("Delete MVEL error: {}", "Unauthorized user");
				return unauthorizedUserResp(responseJson);
			}
			return mvelService.deleteMvelFunction(userSessionDetails,serviceId,id);
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
