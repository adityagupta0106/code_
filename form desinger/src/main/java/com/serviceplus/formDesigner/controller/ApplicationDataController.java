package com.serviceplus.formDesigner.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.autosave.request.dto.ApplicationAutoSaveDTO;
import com.serviceplus.formDesigner.dto.ApplicationAttributeValueRequest;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.request.model.ApplicationData;
import com.serviceplus.formDesigner.service.ApplicationDataService;
import com.serviceplus.formDesigner.util.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
public class ApplicationDataController {
	@Autowired
	private ApplicationDataService applicationDataService;
	
	@PostMapping("/addApplicationData") 
	public ResponseEntity<?> addApplicationData(@RequestBody ApplicationData applicationData, @RequestParam(required = false, defaultValue = "false") Boolean draft, @RequestParam( required = false) String txnId, @RequestParam( required = false) Integer serviceId, @RequestParam( required = false) String taskId,@RequestParam( required = false) String dataId,@RequestParam( required = false) String applId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return applicationDataService.addApplicationData(applicationData,draft,txnId,serviceId, taskId,dataId,applId, request,response);
		
	}
	
	@PostMapping("/getApplicationData")
	public Map<String,Object> getById(@RequestBody ApplicationData applicationData, @RequestParam( required = false) Integer serviceId, HttpServletRequest request, HttpServletResponse response) {
		return applicationDataService.getById(applicationData,serviceId,response,request);
	}
	
	 @GetMapping("/applicationTrackStatus")
	 public List<Object> getAllApplication(HttpServletRequest request,HttpServletResponse response){
		 return applicationDataService.applicationTrackStatus(request,response);
	 }
	 
	 @PostMapping("/v1/application/auto-save")
	 public ResponseEntity<?> autoSave(HttpServletRequest request, HttpServletResponse response, @RequestBody ApplicationAutoSaveDTO applicationAutoSaveDTO){
		 try {
			 UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			 if (userSessionDetails == null) {
					throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
				}
			 return applicationDataService.autoSave(applicationAutoSaveDTO, userSessionDetails);
		 } catch (Exception ex) {
				if (ex instanceof SPRuntimeError) {
					throw ex;
				} else {
					ex.printStackTrace();
					throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
	 }

		@GetMapping("/v1/application/auto-save")
		public ResponseEntity<?> getAutoSave(HttpServletRequest request, HttpServletResponse response,
				@RequestParam Integer serviceId, @RequestParam String taskId, @RequestParam String formId,
				@RequestParam(required = false) String applicationId) {
			try {
				UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
				if (userSessionDetails == null) {
					throw new SPRuntimeError("Unauthorized user", HttpStatus.FORBIDDEN);
				}
				return applicationDataService.getAutoSave(userSessionDetails, serviceId, taskId, formId, applicationId);
			} catch (Exception ex) {
				if (ex instanceof SPRuntimeError) {
					throw ex;
				} else {
					ex.printStackTrace();
					throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		
		@PostMapping("/getApplicationAttributeValue")
		public Map<String, Object> getApplicationAttributeValue(@RequestBody ApplicationAttributeValueRequest request,
				HttpServletRequest servletRequest, HttpServletResponse response) {

			return applicationDataService.getApplicationAttributeValue(request, response, servletRequest);
		}
		
		@Value("${TRACKING_INTERNAL_TOKEN:}")
		private String trackingInternalToken;
		
		private boolean trustedTrackingCaller(String token) {
			return trackingInternalToken != null && !trackingInternalToken.isBlank()
			        && token != null && MessageDigest.isEqual(
			            trackingInternalToken.getBytes(StandardCharsets.UTF_8),
			            token.getBytes(StandardCharsets.UTF_8));
		}

		@PostMapping("/internal/tracking/application-data")
		public ResponseEntity<?> getTrackingApplicationData(@RequestBody Map<String, Object> body,
		        @RequestHeader(value = "X-Tracking-Internal-Token",required = false) String suppliedToken) {

		    // Only trusted internal microservices may access submitted values.
		    if (!trustedTrackingCaller(suppliedToken)) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		    }

		    try {
		        Object serviceIdValue = body.get("serviceId");

		        if (!(serviceIdValue instanceof Number)) {
		            return ResponseEntity.badRequest().build();
		        }

		        String tenantId = (String) body.get("tenantId");
		        Integer serviceId = ((Number) serviceIdValue).intValue();
		        String applId = (String) body.get("applId");

		        Object requestedIds = body.get("attributeIds");

		        if (!(requestedIds instanceof List<?> attributeIds)
		                || attributeIds.isEmpty()
		                || attributeIds.size() > 20
		                || attributeIds.stream().anyMatch(
		                    id -> !(id instanceof String)
		                        || ((String) id).isBlank())) {

		            return ResponseEntity.badRequest().build();
		        }

		        ApplicationData data =applicationDataService.getTrackingApplicationData(tenantId, serviceId, applId);

		        if (!(data.getFormData() instanceof Map<?, ?> formData)) {
		            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		        }

		        Map<String, Object> values = new LinkedHashMap<>();

		        for (Object attributeId : attributeIds) {
		            String id = (String) attributeId;
		            values.put(id, formData.get(id));
		        }

		        Map<String, Object> result = new LinkedHashMap<>();
		        result.put("applicationId", applId);
		        result.put("attributeValues", values);

		        return ResponseEntity.ok(result);

		    } catch (IllegalArgumentException
		             | ClassCastException ex) {

		        return ResponseEntity.badRequest().build();

		    } catch (IllegalStateException ex) {

		        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		    }
		}
		


    @PostMapping("/internal/tracking/match")
    public ResponseEntity<?> matchTrackingApplications(@RequestBody Map<String,Object> body,
        @RequestHeader(value="X-Tracking-Internal-Token",required=false) String token) {
        if (!trustedTrackingCaller(token))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {return ResponseEntity.ok(applicationDataService.matchTrackingApplications(body));}
        catch(IllegalArgumentException ex) { ex.printStackTrace();return ResponseEntity.badRequest().body(Map.of("message","Invalid verification request"));}
        catch(IllegalStateException ex) {return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message","Submission verification unavailable"));}
    }
}
