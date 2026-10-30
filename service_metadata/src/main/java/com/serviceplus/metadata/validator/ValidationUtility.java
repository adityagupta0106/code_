package com.serviceplus.metadata.validator;

public class ValidationUtility {
	public final static String REG_EX_ALPHANUMERIC = "^[0-9 A-Za-z]+$";
	
	public static boolean checkEmpty(Object value){
		value = value!=null?value.toString().trim():"";
		if(value.equals("")){
			return true;
		}
		return false;
	}	
	
	
	public static boolean hasValidValue(Object value,String regEx){
		 String valueStr = value!=null?value.toString().trim():"";
		 if(!checkEmpty(value)){
			 return valueStr.matches(regEx);
		 }
		 return false;
	 }

}
