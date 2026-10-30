package com.serviceplus.metadata.enums;

public enum ExternalSystemRedirectMethodType {
	GET("GET", "GET"), 
	POST("POST", "POST");

	private final String code;
	private final String label;

	ExternalSystemRedirectMethodType(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	public static ExternalSystemRedirectMethodType fromCode(String code) {
		for (ExternalSystemRedirectMethodType flag : ExternalSystemRedirectMethodType.values()) {
			if (flag.code.equalsIgnoreCase(code)) {
				return flag;
			}
		}
		throw new IllegalArgumentException("Invalid redirect flag code: " + code);
	}
	
	public static boolean isValid(String code) {

	    for (ExternalSystemRedirectMethodType type : values()) {
	        if (type.code.equalsIgnoreCase(code)) {
	            return true;
	        }
	    }

	    return false;
	}
}
