package com.serviceplus.formDesigner.dto;

public class InValidEncKeyResponseDTO {

	private String encryptionKey;
	private Object formData;
	public String getEncryptionKey() {
		return encryptionKey;
	}
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	public Object getFormData() {
		return formData;
	}
	public void setFormData(Object formData) {
		this.formData = formData;
	}
	
}
