package com.serviceplus.formDesigner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.request.model.LanguagePacksMaster;
import com.serviceplus.formDesigner.service.LanguageMasterService;
import com.serviceplus.formDesigner.service.LanguagesService;


@RestController
public class LanguageMasterController {

	@Autowired private LanguagesService languagesService;

	@Autowired private LanguageMasterService languageMasterService;

	@GetMapping("/languages")
	public List<Object> languages(){
		return languagesService.getAllActiveLanguages();
	}
	
	@GetMapping("/languageList")
	public List<Object> languageList(){
		return languageMasterService.languageList();
	}
	
	@GetMapping("/getLanguagePacksMaster")
	public LanguagePacksMaster getLanguagePacksMaster(){
		return languageMasterService.getLanguagePacksMaster();
	}
	
	@PostMapping("/addLanguagePacksMaster")
	public String languageList(@RequestBody LanguagePacksMaster languagePacksMaster){
		return languageMasterService.addLanguagesPacksMaster(languagePacksMaster);
	}
}
