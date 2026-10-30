package com.serviceplus.formDesigner.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplus.formDesigner.repository.LanguageMasterRepository;
import com.serviceplus.formDesigner.repository.LanguagePacksMasterRepository;
import com.serviceplus.formDesigner.request.model.LanguageMaster;
import com.serviceplus.formDesigner.request.model.LanguagePacksMaster;
import com.serviceplus.formDesigner.util.ApplicationConstants;
import com.serviceplus.formDesigner.util.MessageConstants;

@Service
public class LanguageMasterService {

	@Autowired
	private LanguageMasterRepository languageMasterRepository;
	@Autowired
	private LanguagePacksMasterRepository languagePacksMasterRepository;
	
	public List<Object> languageList(){
		List<Object> responseData = new LinkedList<Object>();
		try {
			List<LanguageMaster> lanList = getAllLanguageMaster();
			for(LanguageMaster language : lanList) {
				HashMap<String,Object> map = new HashMap<String, Object>();
				map.put("key", language.getLangShortName());
				map.put("Value", language.getLangName());
				
				responseData.add(map);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return responseData;
	}
	
	public List<LanguageMaster> getAllLanguageMaster() {
		return languageMasterRepository.findAll();
	}
	
	public String addLanguagesPacksMaster(LanguagePacksMaster languagePacksMaster) {
		try {
			languagePacksMasterRepository.save(languagePacksMaster);
			return MessageConstants.DATA_SAVE;
		}catch (Exception e) {
			e.printStackTrace();
			return MessageConstants.DATA_NOT_SAVE;
		}
	}

	public LanguagePacksMaster getLanguagePacksMaster() {
		LanguagePacksMaster languagePacksMaster = null;
		try {
			languagePacksMaster = languagePacksMasterRepository.findById(ApplicationConstants.LANGUAGE_PACK_MASTER_ID).get();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return languagePacksMaster;
	}
}
