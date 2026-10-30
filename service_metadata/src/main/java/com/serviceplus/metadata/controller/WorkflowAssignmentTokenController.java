package com.serviceplus.metadata.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.DesignatedOfficerAssignmentDTO;
import com.serviceplus.metadata.dto.FetchTaskHolders;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.dto.WorkflowAssignmentDTO;
import com.serviceplus.metadata.dto.WorkflowAssignmentTokenRequest;
import com.serviceplus.metadata.dto.WorkflowAssignmentTokenResponse;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.WorkflowAssignmentService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.serviceplus.metadata.dto.UserAssignmentValidationRequestDTO;
import com.serviceplus.metadata.service.UserAssignmentControlService;

@RestController
@RequestMapping("/workflow")
public class WorkflowAssignmentTokenController {

	private final WorkflowAssignmentService workflowAssignmentService;
	private final UserAssignmentControlService userAssignmentControlService;

	public WorkflowAssignmentTokenController(WorkflowAssignmentService workflowAssignmentService,
											 UserAssignmentControlService userAssignmentControlService) {
		this.workflowAssignmentService = workflowAssignmentService;
		this.userAssignmentControlService = userAssignmentControlService;
	}

	@PostMapping("/assign")
	public ResponseEntity<WorkflowAssignmentTokenResponse> create(@RequestBody WorkflowAssignmentTokenRequest request,
			HttpServletRequest httpRequest) {

		try {
			return ResponseEntity.ok(workflowAssignmentService.create(request, httpRequest));
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			}

			e.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/fetch")
	public ResponseEntity<WorkflowAssignmentTokenResponse> fetchAssignments(@RequestParam Integer serviceId, @RequestParam(required = false) String taskId,
			@RequestParam(required = false) Integer levelId, @RequestParam(required = false) String locationId, HttpServletRequest request) {

		return ResponseEntity.ok(workflowAssignmentService.fetchAssignments(serviceId, taskId, levelId, locationId, request));
	}

	
	@GetMapping("/assignment-details")
	public ResponseEntity<List<WorkflowAssignmentDTO>> getAssignmentDetails(@RequestParam Integer serviceId,
			@RequestParam String taskId, @RequestParam String locationId) {

		return ResponseEntity.ok(workflowAssignmentService.getAssignments(serviceId, taskId, locationId));
	}

    @PostMapping("/fetchTaskHolders")
    public ResponseEntity<FetchTaskHolders> fetchTaskHolders(
            @RequestParam Integer serviceId,
            @RequestParam String taskId,
            HttpServletRequest request) {

        FetchTaskHolders response = workflowAssignmentService.fetchLocationsHolders(serviceId, taskId, request);
        return ResponseEntity.ok(response);
    }
    
	@PostMapping("/assign-designated-officer")
	public ResponseEntity<?> assignDesignatedOfficer(@RequestBody @Valid DesignatedOfficerAssignmentDTO assignmentDTO,
			HttpServletRequest request) {
		try {
    		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    		if (userSessionDetails == null) {
    			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
    		return workflowAssignmentService.assignDesignatedOfficer(assignmentDTO, userSessionDetails);
    	}catch (Exception ex) {
    		if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
    	}		
	}
	
	@PostMapping("/unassign-designated-officer")
	public ResponseEntity<?> unassignDesignatedOfficer(@RequestBody @Valid DesignatedOfficerAssignmentDTO assignmentDTO,
			HttpServletRequest request) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return workflowAssignmentService.unassignDesignatedOfficer(assignmentDTO, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/designated-officer-services")
	public ResponseEntity<?> getAssignedServices(@RequestParam(required=false)Integer status, HttpServletRequest request) {
	    try {
	        UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
	        if (userSessionDetails == null) {
	            throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
	        }
	        return workflowAssignmentService.getAssignedServices(status,userSessionDetails);
	    } catch (Exception ex) {
	        if (ex instanceof SPRuntimeError) {
	            throw ex;
	        }
	        ex.printStackTrace();
	        throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("/do-service-delivery-units")
	public ResponseEntity<?> getAssignedServiceDeliveryUnits(@RequestParam Integer serviceId,
			HttpServletRequest request) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}

			return workflowAssignmentService.getAssignedServiceDeliveryUnits(serviceId, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/do-service-delivery-unit-offices")
	public ResponseEntity<?> getAssignedOfficeDetails(@RequestParam Integer serviceId,
			@RequestParam Integer deliveryUnitLevelId,@RequestParam(required = false) Integer status, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "20") Integer size, @RequestParam(required = false) String search,
			HttpServletRequest request) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return workflowAssignmentService.getAssignedOfficeDetails(serviceId, deliveryUnitLevelId, page, size,
					search, userSessionDetails,status);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/validate-assignment")
	public ResponseEntity<?> validateAssignment(@Valid @RequestBody UserAssignmentValidationRequestDTO request,
												HttpServletRequest httpRequest) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(httpRequest);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return userAssignmentControlService.validateUserAssignmentEligibility(request, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
