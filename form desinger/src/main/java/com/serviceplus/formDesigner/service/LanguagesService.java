package com.serviceplus.formDesigner.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplus.formDesigner.repository.LanguagesRepository;
import com.serviceplus.formDesigner.request.model.Languages;

@Service
public class LanguagesService {

	@Autowired
	private LanguagesRepository languagesRepository;
	
	public List<Object> getAllActiveLanguages(){
		List<Object> response = new ArrayList<Object>();
		List<Languages> activeLanguageslist = new ArrayList<Languages>();
		try {
			activeLanguageslist = languagesRepository.findByIsActive(true);
			for(Languages languages : activeLanguageslist) {
				Map<String, Object> data = new HashMap<String, Object>(); 
				data.put("id", languages.getId());
				data.put("value", languages.getValue());
				
				response.add(data);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}
