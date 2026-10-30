package com.serviceplus.metadata.dto;
public class AllOfficeLocationDto {

	private String locname;
	private String parentName;
	private Integer locid;
	
	public AllOfficeLocationDto() {
		
	}
	
	public AllOfficeLocationDto(Integer locid,String locname, String parentName) {
		super();
		this.locname = locname;
		this.parentName = parentName;
		this.locid = locid;
	}

	public String getLocname() {
		return locname;
	}
	public void setLocname(String locname) {
		this.locname = locname;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Integer getLocid() {
		return locid;
	}
	public void setLocid(Integer locid) {
		this.locid = locid;
	}
	
	
}

