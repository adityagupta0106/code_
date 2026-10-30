package com.serviceplus.metadata.formrecommendation.controller;

import org.json.JSONObject;
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
import com.serviceplus.metadata.formrecommendation.service.FormRecommendationService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/recommend")
public class FormRecommendationController {
	@Autowired
	private FormRecommendationService formRecommendationService;
	
	@GetMapping("/srvs")
	public ResponseEntity<?> getServiceList(HttpServletRequest request,HttpServletResponse response){
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
            	responseJson.put("errorMessage", "Something went wrong");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return formRecommendationService.getServiceList(userSessionDetails);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/srvs/forms")
	public ResponseEntity<?> getAllFormsOfService(HttpServletRequest request,HttpServletResponse response, @RequestParam(name = "srvid") Integer srvid){
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
            	responseJson.put("errorMessage", "Something went wrong");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return formRecommendationService.getAllFormsOfService(userSessionDetails, srvid);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/searchforms")
	public ResponseEntity<?> searchforms(HttpServletRequest request,HttpServletResponse response, @RequestBody String searchQuery){
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
	        	responseJson.put("errorMessage", "Something went wrong");
	        	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return formRecommendationService.searchforms(userSessionDetails, searchQuery);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
