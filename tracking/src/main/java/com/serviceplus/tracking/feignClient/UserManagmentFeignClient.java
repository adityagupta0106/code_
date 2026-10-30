package com.serviceplus.tracking.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "UserManagement", path = "/usermgmt", url = "${usermanagement.service.url}")
public interface UserManagmentFeignClient {

	@GetMapping("/checkUserAssigned")
	public ResponseEntity<?> checkUserAssigned(@RequestParam String userToken ,@RequestParam Long userId);

    @PostMapping("/internal/tracking/otp/send/email")
    ResponseEntity<Map<String, Object>> sendTrackingEmailOtp(@RequestHeader("X-Tracking-Internal-Token") String token,@RequestBody Map<String, Object> request);

    // Phase 3 - Verify OTP
    @PostMapping("/internal/tracking/otp/verify")
    ResponseEntity<Map<String, Object>> verifyTrackingOtp(@RequestHeader("X-Tracking-Internal-Token") String token,@RequestBody Map<String, Object> request);

    @PostMapping("/internal/tracking/otp/send")
    ResponseEntity<Map<String,Object>> sendTrackingOtp(@RequestHeader("X-Tracking-Internal-Token") String token,
        @RequestBody Map<String,Object> request);
}
