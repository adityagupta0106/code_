package com.serviceplus.metadata.departmentConfig.dto;

public class WebServiceDetailsResponseDTO {
	
	 private String id;
	 private String templateName;
	 private String method;
	 public String getId() {
		 return id;
	 }
	 public void setId(String id) {
		 this.id = id;
	 }
	 
	 public String getTemplateName() {
		 return templateName;
	 }
	 public void setTemplateName(String templateName) {
		 this.templateName = templateName;
	 }
	 public String getMethod() {
		 return method;
	 }
	 public void setMethod(String method) {
		 this.method = method;
	 }
	 
}
