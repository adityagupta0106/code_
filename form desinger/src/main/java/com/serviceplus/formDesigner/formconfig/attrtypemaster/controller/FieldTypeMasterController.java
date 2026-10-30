package com.serviceplus.formDesigner.formconfig.attrtypemaster.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.dto.UserSessionDTO.Roles;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.service.FieldJSONMasterService;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.service.FieldTypeMasterService;
import com.serviceplus.formDesigner.util.CommonUtil;
import com.serviceplus.formDesigner.util.MessageConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping("/v1")
public class FieldTypeMasterController {

	@Autowired
	private FieldTypeMasterService fieldTypeMasterService;
	
	@Autowired
	private FieldJSONMasterService fieldJSONMasterService;

	@GetMapping(value = "/regex-master")
	public ResponseEntity<?> getRegexMaster(HttpServletRequest request, HttpServletResponse response) {
		return fieldTypeMasterService.getRegexMaster();
	}

	@GetMapping(value = "/field-type-master")
	public ResponseEntity<?> getFieldTypeMaster(HttpServletRequest request, HttpServletResponse response) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				throw new SPRuntimeError(MessageConstants.UNAUTHORIZED_USER.toString(), HttpStatus.FORBIDDEN);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				throw new SPRuntimeError(MessageConstants.UNAUTHORIZED_USER.toString(), HttpStatus.FORBIDDEN);
			}
			return fieldTypeMasterService.getFieldTypeMaster(userSessionDetails.getTenantId());
		} catch (Exception ex) {
			if(ex instanceof SPRuntimeError) {
				throw ex;
			}
			else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	@GetMapping(value = "/field-json")                                                                                                                               
	public ResponseEntity<?> getFieldTypeMJson(@RequestParam Integer typeId, HttpServletRequest request, HttpServletResponse response) {
		
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			
			if (userSessionDetails == null) {
				throw new SPRuntimeError(MessageConstants.UNAUTHORIZED_USER.toString(), HttpStatus.FORBIDDEN);
			}
			boolean hasRole = userSessionDetails.getRoles().stream().anyMatch(role -> role.getRoleId() == 9);
			if (!hasRole) {
				throw new SPRuntimeError(MessageConstants.UNAUTHORIZED_USER.toString(), HttpStatus.FORBIDDEN);
			}
			return fieldJSONMasterService.getRecordByTypeId(typeId,userSessionDetails.getTenantId());
		} catch (Exception ex) {
			if(ex instanceof SPRuntimeError) {
				throw ex;
			}
			else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
}
