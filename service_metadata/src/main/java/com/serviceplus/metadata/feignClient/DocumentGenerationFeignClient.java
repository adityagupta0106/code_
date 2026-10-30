package com.serviceplus.metadata.feignClient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "GENERATEDOCUMENT", path = "/generateDocument")
public interface DocumentGenerationFeignClient {

	@PostMapping("/get-html-to-pdf")
	ResponseEntity<Map<String, Object>> convertHtmlToPdf(@RequestBody Map<String, String> request);

}
