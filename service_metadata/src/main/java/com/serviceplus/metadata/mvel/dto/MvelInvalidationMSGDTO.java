package com.serviceplus.metadata.mvel.dto;

import java.util.List;
import java.util.Map;

public class MvelInvalidationMSGDTO {
	private String funDefMsg;
	private String funBodyMsg;
	private List<Map<String, Object>> funParaMsg;
	public String getFunDefMsg() {
		return funDefMsg;
	}
	public void setFunDefMsg(String funDefMsg) {
		this.funDefMsg = funDefMsg;
	}
	public String getFunBodyMsg() {
		return funBodyMsg;
	}
	public void setFunBodyMsg(String funBodyMsg) {
		this.funBodyMsg = funBodyMsg;
	}
	public List<Map<String, Object>> getFunParaMsg() {
		return funParaMsg;
	}
	public void setFunParaMsg(List<Map<String, Object>> funParaMsg) {
		this.funParaMsg = funParaMsg;
	}
	
}
