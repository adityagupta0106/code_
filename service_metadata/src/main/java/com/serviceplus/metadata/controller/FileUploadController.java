package com.serviceplus.metadata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.service.FileUploadService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/metadata")
public class FileUploadController {
	private final FileUploadService fileUploadService;

	public FileUploadController(FileUploadService fileUploadService) {
		super();
		this.fileUploadService = fileUploadService;
	}

	@GetMapping("/module-upload-sessions")
	public ResponseEntity<?> getUploadIds(@RequestParam Long moduleId,HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);	  
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
			if (userSessionDetails == null) {
	            return ResponseEntity.badRequest().body("Bad Request");
			}
			return fileUploadService.getUploadIds(moduleId, userSessionDetails);
		} catch (Exception ex) {
			return ResponseEntity.internalServerError().body(ex.getMessage());
		}
	}
	  private UserSessionDTO getLocalSessionDetails(UserSessionDTO userSessionDetails) {
//	    	userSessionDetails=new UserSessionDTO();
//	    	userSessionDetails.setUserID(-2);
//	    	userSessionDetails.setStateId(32);
//	    	userSessionDetails.setDepartmentId(544);
//	    	userSessionDetails.setTenantId("69083a6acc540a2387c48e52");
			return userSessionDetails;
		} 
}
