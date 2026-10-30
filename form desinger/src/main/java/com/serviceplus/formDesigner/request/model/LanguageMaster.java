package com.serviceplus.formDesigner.request.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LanguageMaster {

	@Id
	private String id;
	
	private String langName;
	private String langShortName;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLangName() {
		return langName;
	}
	public void setLangName(String langName) {
		this.langName = langName;
	}
	public String getLangShortName() {
		return langShortName;
	}
	public void setLangShortName(String langShortName) {
		this.langShortName = langShortName;
	}
	
	
}
