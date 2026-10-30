package com.serviceplus.metadata.dto;

public class XmlNodeDTO {
	private String nodeCode;
	private String xpath;
	private MappingType mappingType;
	private Object mappingValue;
	private String displayName;
	private String dataType;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public MappingType getMappingType() {
		return mappingType;
	}

	public void setMappingType(MappingType mappingType) {
		this.mappingType = mappingType;
	}

	public Object getMappingValue() {
		return mappingValue;
	}

	public void setMappingValue(Object mappingValue) {
		this.mappingValue = mappingValue;
	}

	public enum MappingType {
		NONE, STATIC, DYNAMIC, SYSTEM_GENERATED
	}
}