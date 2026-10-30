package com.serviceplus.tracking.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.ServiceDropdownDTO;
import com.serviceplus.tracking.dto.TaskDropdownDTO;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.service.SentBoxFilterService;
import com.serviceplus.tracking.service.ServiceTaskFilterService;
import com.serviceplus.tracking.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/a/sentbox")
public class SentBoxFilterController {

	private static final Logger logger = LoggerFactory.getLogger("sentboxLogger");
	
    private final SentBoxFilterService service;

    @Autowired
    private ServiceTaskFilterService serviceTaskFilterService;
    
    @Autowired
    public SentBoxFilterController(SentBoxFilterService service) {
        this.service = service;
    }

    @PostMapping("/services")
    public List<ServiceDropdownDTO> getServices(HttpServletRequest request) {
        return service.getServices(request);
    }

    @PostMapping("/tasks")
    public List<TaskDropdownDTO> getTasks(@RequestParam Integer baseServiceId, HttpServletRequest request) {
        return service.getTasks(baseServiceId, request);
    }
    
    @GetMapping("/filter-list")
	public ResponseEntity<?> getFilterAttribute(@RequestParam Long serviceId,@RequestParam String taskId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized", HttpStatus.UNAUTHORIZED);
			}
			return serviceTaskFilterService.getFilterAttribute(serviceId, taskId, 'I', List.of('S','B'));
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("Error get Filter Attributes", ex);
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
}
