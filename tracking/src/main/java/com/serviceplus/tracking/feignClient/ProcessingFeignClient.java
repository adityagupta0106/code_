package com.serviceplus.tracking.feignClient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.serviceplus.tracking.dto.HandlerResponse;
import com.serviceplus.tracking.dto.ServiceMeta;


@FeignClient(name = "PROCESSING", path = "/processing", url = "${processing.service.url}")
public interface ProcessingFeignClient {

	@PostMapping("/a/apply/serviceKey")
	ResponseEntity<ServiceMeta> fetchServiceKey(@RequestHeader Map<String, Object> headers,@RequestParam Integer baseServiceId,@RequestParam(required = false) String applicationId,
			@RequestParam(required = false) String taskId,@RequestParam(required = false) String serviceId);
	
	@PostMapping("/a/form/render")
	ResponseEntity<HandlerResponse> render(@RequestHeader Map<String, Object> headers,@RequestParam String serviceKey,@RequestParam String serviceId);
	

}
