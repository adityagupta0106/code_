package com.serviceplus.metadata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.ExternalClientApiDetailDTO;
import com.serviceplus.metadata.dto.ExternalSystemUserDTO;
import com.serviceplus.metadata.service.ExternalClientApiService;

@RestController
public class ExternalClientApiController {

	private final ExternalClientApiService externalClientApiservice;

	public ExternalClientApiController(ExternalClientApiService externalClientApiservice) {
		this.externalClientApiservice = externalClientApiservice;
	}

	@GetMapping("/external-client-api-registration")
	public ExternalClientApiDetailDTO getExternalClientApiDetail(@RequestParam String clientId, @RequestParam Integer apiType) {
		return externalClientApiservice.getExternalClientApiDetail(clientId, apiType);
	}
	
	@GetMapping("/external-system-user")
    public ExternalSystemUserDTO getByClientId(@RequestParam String clientId) {
		return externalClientApiservice.getByClientId(clientId);
	}
	
	
	
}
