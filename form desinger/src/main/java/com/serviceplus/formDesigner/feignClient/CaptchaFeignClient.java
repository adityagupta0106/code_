package com.serviceplus.formDesigner.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="Authentication",path="/authentication")
public interface CaptchaFeignClient {

	@GetMapping("/a/captcha")
	ResponseEntity<?> generateCaptcha(@RequestHeader("USER-DETAILS") String userDetails,@RequestHeader("Internal-Call") String internalCall,@RequestParam(required=false) String removeCaptcha);
}
