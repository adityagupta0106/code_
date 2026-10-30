package com.serviceplus.metadata.feignClient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.serviceplus.metadata.dto.FormEncryptionKeyDTO;
import com.serviceplus.metadata.dto.ServiceTemplateMappingDTO;
import com.serviceplus.metadata.notification.dto.SpecificAttributesReqDTO;

@FeignClient(name = "formDesigner",path = "/formmgmt")
public interface FormDesignerFeignClient {

	@PostMapping("/saveInstantiableForm")
	public ResponseEntity<Map<String,Object>> saveInstantiableForm(@RequestBody ServiceTemplateMappingDTO serviceTemplateMapping);
	
	@GetMapping("/getAllAttributesByFormId")
	public ResponseEntity<?> getAllAttributesByFormId(@RequestParam String formId);
	
	@PostMapping("/saveFormEncryptionKey")
	public ResponseEntity<Map<String,Object>> saveFormEncryptionKey(@RequestBody FormEncryptionKeyDTO formEncryptionDTO);
	
	@PostMapping("/api/v1/form/subscribe")
	public ResponseEntity<?> formSubscribe(@RequestParam String formId);
	
	@PostMapping("/api/v1/form/un-subscribe")
	public ResponseEntity<?> formUnSubscribe(@RequestParam String formId, @RequestParam(required = false) Boolean makeCopy);
	
	@PostMapping("/specific-attributes")
	public ResponseEntity<?> getSpecificAttributes(@RequestBody SpecificAttributesReqDTO specificAttributesReqDTO);
}
