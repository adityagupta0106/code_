package com.serviceplus.tracking.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.serviceplus.tracking.dto.FormAttributeRequest;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "formDesigner", path = "/formmgmt", url = "${formdesigner.service.url}")
public interface FormDesignerFeignClient {

	@PostMapping("/filter-attributes")
	public ResponseEntity<?> getFilterAttributes(@RequestBody FormAttributeRequest formAttributeRequest);


	@PostMapping("/internal/tracking/application-data")
	ResponseEntity<Map<String, Object>> getTrackingApplicationData( @RequestHeader("X-Tracking-Internal-Token") String token, @RequestBody Map<String, Object> request);

    @PostMapping("/internal/tracking/match")
    ResponseEntity<Map<String,Object>> matchTrackingApplications(@RequestHeader("X-Tracking-Internal-Token") String token,
        @RequestBody Map<String,Object> request);
}
