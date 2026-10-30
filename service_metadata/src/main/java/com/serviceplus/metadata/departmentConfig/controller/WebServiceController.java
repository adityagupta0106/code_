package com.serviceplus.metadata.departmentConfig.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.departmentConfig.dto.WebServiceDetailsResponseDTO;
import com.serviceplus.metadata.departmentConfig.entity.WebServiceDetails;
import com.serviceplus.metadata.departmentConfig.service.WebServiceDetailsService;
import com.serviceplus.metadata.dto.ExternalSystemAuthReqDTO;
import com.serviceplus.metadata.dto.ExternalSystemGeneralReqDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class WebServiceController {

	@Autowired
	private WebServiceDetailsService wsDetailService;

	@PostMapping("/saveWebServiceDetail")
	public ResponseEntity<?> saveWebServiceDetail(@RequestBody WebServiceDetails wsDetail, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			//userSessionDetails=getLocalSessionDetails(userSessionDetails);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			wsDetail.setTenantId(userSessionDetails.getTenantId());
			WebServiceDetails wsDetails = wsDetailService.saveWSDetail(wsDetail);
			if (wsDetails.getId() != null) {
				responseJson.put("status", "200");
				responseJson.put("message", "Web Service saved successfully");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
			} else {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/webservicelist")
	public ResponseEntity<?> getWebServiceList(HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			//userSessionDetails=getLocalSessionDetails(userSessionDetails);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			List<WebServiceDetailsResponseDTO> wsDetailList = wsDetailService.getByTenantId(userSessionDetails.getTenantId());
			if (wsDetailList.isEmpty()) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "No web services found for tenant");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return ResponseEntity.ok(wsDetailList);
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/wsDetailsById")
	public ResponseEntity<?> getWsDetailsById(HttpServletRequest request, @RequestParam String id) {

		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			//userSessionDetails=getLocalSessionDetails(userSessionDetails);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			List<WebServiceDetails> wsDetailList = wsDetailService.findByIdAndTenantId(id,
					userSessionDetails.getTenantId());
			if (wsDetailList.isEmpty()) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Invalid Webservice Id");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return ResponseEntity.ok(wsDetailList.get(0));
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/get-all-external-system")
	public ResponseEntity<?> getAllExternalSystem(HttpServletRequest request, @RequestParam(required = false) Integer status){
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
            	responseJson.put("errorMessage", "Something went wrong");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			
			return wsDetailService.getAllExternalSystem(userSessionDetails,status);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("/get-external-system")
	public ResponseEntity<?> getExternalSystem(HttpServletRequest request,@RequestParam String clientId, @RequestParam Integer tabId){
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
            	responseJson.put("errorMessage", "Something went wrong");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return wsDetailService.getExternalSystem(userSessionDetails, clientId, tabId);
			
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
			
	}
	
	@GetMapping("/external-system-status")
	public ResponseEntity<?> changeStatus(HttpServletRequest request, @RequestParam String clientId){
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
            	responseJson.put("errorMessage", "Something went wrong");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return wsDetailService.changeStatus(userSessionDetails, clientId);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	@PostMapping("/register-external-system")
	public ResponseEntity<?> registerExternalSystem(HttpServletRequest request, @RequestBody ExternalSystemGeneralReqDTO externalSystemReqDTO){
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
            	responseJson.put("errorMessage", "Something went wrong");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return wsDetailService.registerExternalSystem(userSessionDetails,externalSystemReqDTO);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@PostMapping("/external-system-auth")
	public ResponseEntity<?> saveAuthDetail(HttpServletRequest request, @RequestBody ExternalSystemAuthReqDTO authReqDTO){
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
            	responseJson.put("errorMessage", "Something went wrong");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return wsDetailService.saveAuthDetail(userSessionDetails,authReqDTO,1);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@PostMapping("/external-system-request")
	public ResponseEntity<?> saveRequestDetail(HttpServletRequest request, @RequestBody ExternalSystemAuthReqDTO authReqDTO){
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
				responseJson.put("errorMessage", "Something went wrong");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return wsDetailService.saveAuthDetail(userSessionDetails,authReqDTO,2);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@PostMapping("/external-system-draft")
	public ResponseEntity<?> saveDraftApplicationRequestDetail(HttpServletRequest request, @RequestBody ExternalSystemAuthReqDTO authReqDTO){
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
				responseJson.put("errorMessage", "Something went wrong");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return wsDetailService.saveAuthDetail(userSessionDetails,authReqDTO,3);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}  
}