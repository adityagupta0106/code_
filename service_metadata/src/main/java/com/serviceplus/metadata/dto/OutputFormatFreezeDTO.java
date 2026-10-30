package com.serviceplus.metadata.dto;

import java.util.List;

import com.serviceplus.metadata.dto.ServiceOutputFormatDTO.JsonNodeConfig;
import com.serviceplus.metadata.enums.JsonRootType;

public class OutputFormatFreezeDTO {

	private Long id;

	private Integer layout;

	private String pageSize;

	private List<String> usedAttr;

	private String templateName;

	private String documentType;

	private String templateBodySrc;

	private String waterMark;

	private String xsd;

	private List<XmlNodeDTO> xmlNodes;
	
	private JsonRootType rootType;
	
	private List<JsonNodeConfig> jsonNodes;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLayout() {
		return layout;
	}

	public void setLayout(Integer layout) {
		this.layout = layout;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getUsedAttr() {
		return usedAttr;
	}

	public void setUsedAttr(List<String> usedAttr) {
		this.usedAttr = usedAttr;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getTemplateBodySrc() {
		return templateBodySrc;
	}

	public void setTemplateBodySrc(String templateBodySrc) {
		this.templateBodySrc = templateBodySrc;
	}

	public String getWaterMark() {
		return waterMark;
	}

	public void setWaterMark(String waterMark) {
		this.waterMark = waterMark;
	}

	public String getXsd() {
		return xsd;
	}

	public void setXsd(String xsd) {
		this.xsd = xsd;
	}

	public List<XmlNodeDTO> getXmlNodes() {
		return xmlNodes;
	}

	public void setXmlNodes(List<XmlNodeDTO> xmlNodes) {
		this.xmlNodes = xmlNodes;
	}

	public List<JsonNodeConfig> getJsonNodes() {
		return jsonNodes;
	}

	public void setJsonNodes(List<JsonNodeConfig> jsonNodes) {
		this.jsonNodes = jsonNodes;
	}

	public JsonRootType getRootType() {
		return rootType;
	}

	public void setRootType(JsonRootType rootType) {
		this.rootType = rootType;
	}

}
