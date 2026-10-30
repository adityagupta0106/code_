package com.serviceplus.metadata.enums;

public enum ExternalSystemParamType {
	 HEAD ("H", "HEAD"), 
	BODY("B", "BODY");

	private final String code;
	private final String label;
	
	ExternalSystemParamType(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}
	public static boolean isValid(String code) {

	    for (ExternalSystemParamType type : values()) {
	        if (type.code.equalsIgnoreCase(code)) {
	            return true;
	        }
	    }

	    return false;
	}
}
