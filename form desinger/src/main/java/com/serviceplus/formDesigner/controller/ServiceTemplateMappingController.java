package com.serviceplus.formDesigner.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.dto.FormEncryptionKeyDTO;
import com.serviceplus.formDesigner.dto.ServiceTemplateMappingDTO;
import com.serviceplus.formDesigner.service.ServiceTemplateMappingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ServiceTemplateMappingController {

	@Autowired
	private ServiceTemplateMappingService applicationTemplateMappingService;
	
	@PostMapping("/isFormExistInDepartment")
	public ResponseEntity<?> isFormExistInDepartment(@RequestBody ServiceTemplateMappingDTO serviceTemplateMapping){
		return applicationTemplateMappingService.isFormExistInDepartment(serviceTemplateMapping);
	}
	@GetMapping("/getFormsByDepartmentId")
	public ResponseEntity<?> getFormsByDepartmentId(HttpServletRequest request){
		return applicationTemplateMappingService.getAllTemplateInDepartment(request);
	}

	@PostMapping("/saveInstantiableForm")
	public ResponseEntity<Map<String,Object>> saveInstantiableForm(@RequestBody ServiceTemplateMappingDTO serviceTemplateMapping){
		return applicationTemplateMappingService.saveInstantiableForm(serviceTemplateMapping);
	}
	
	@PostMapping("/saveFormEncryptionKey")
	public ResponseEntity<Map<String, Object>> saveFormEncryptionKey(@Valid @RequestBody FormEncryptionKeyDTO formEncryptionKeyDTO){
		return applicationTemplateMappingService.saveFormEncryptionKey(formEncryptionKeyDTO);
	}
}
