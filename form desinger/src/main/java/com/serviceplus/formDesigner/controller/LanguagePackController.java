package com.serviceplus.formDesigner.controller;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.request.model.LanguagePacks;
import com.serviceplus.formDesigner.service.LanguagePacksService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class LanguagePackController {
	@Autowired
	private LanguagePacksService languagePacksService;
	
	@GetMapping("/languagePackByFormId")
	public Object getLanguagePackByFormId(@RequestParam String formId,HttpServletResponse response) {
		return languagePacksService.getLanguagePackByFormId(formId, response);
	}
	@GetMapping("/languagePacksById")
	public Map<String,Object> getLanguagePacksById(@RequestParam String id,HttpServletResponse response){
		return languagePacksService.getLanguagePacks(id,response);
	}
	@PostMapping("/addLanguagePacks")
	public Map<String,Object> addLanguagePacks(@RequestBody LanguagePacks languagePacks,HttpServletResponse httpServletResponse){
		return languagePacksService.addLanguagePacks(languagePacks,httpServletResponse);
	}
}
