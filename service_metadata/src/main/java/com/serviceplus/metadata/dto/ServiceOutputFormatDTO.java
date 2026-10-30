package com.serviceplus.metadata.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.enums.JsonDataType;
import com.serviceplus.metadata.enums.JsonNodeSource;
import com.serviceplus.metadata.enums.JsonRootType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceOutputFormatDTO {

	private Integer serviceId;

	private List<String> tabList;

	private Long outputFormatId;

	@NotNull(message = "Template type cannot be empty")
	private Integer templateType;

	@NotEmpty(message = "Template name cannot be empty")
	private String templateName;

	// @NotNull(message = "Document type cannot be empty")
	private String documentType;

	private Integer layout;

	private String pageSize;

	private String waterMark;

	private String waterMarkOpacity;

	private Integer pdfMargins;

	private CustomMargin customMargin;

	private String templateBodySrc;

	private List<String> usedAttr;

	private String notificationType;

	private String mailSubject;

	private String smsTempId;

	private String xsd;

	private List<XmlNodeDTO> xmlNodes;
	
	private String jsonStr;
	
	private JsonRootType rootType;
	
	private List<JsonNodeConfig> jsonNodes;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class JsonNodeConfig {

		private String nodeCode;

		private String keyName;

		private JsonDataType dataType;

		private JsonNodeSource source;

		private Object value;

		private Boolean required;

		private Integer orderIndex;

		private String parentNodeCode;
		
		private String taskId;
		
		private String documentId;
		
		private Integer typeId;

		private List<JsonNodeConfig> children;

		public String getNodeCode() {
			return nodeCode;
		}

		public void setNodeCode(String nodeCode) {
			this.nodeCode = nodeCode;
		}

		public String getKeyName() {
			return keyName;
		}

		public void setKeyName(String keyName) {
			this.keyName = keyName;
		}

		public JsonDataType getDataType() {
			return dataType;
		}

		public void setDataType(JsonDataType dataType) {
			this.dataType = dataType;
		}

		public JsonNodeSource getSource() {
			return source;
		}

		public void setSource(JsonNodeSource source) {
			this.source = source;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public Boolean getRequired() {
			return required;
		}

		public void setRequired(Boolean required) {
			this.required = required;
		}

		public Integer getOrderIndex() {
			return orderIndex;
		}

		public void setOrderIndex(Integer orderIndex) {
			this.orderIndex = orderIndex;
		}

		public String getParentNodeCode() {
			return parentNodeCode;
		}

		public void setParentNodeCode(String parentNodeCode) {
			this.parentNodeCode = parentNodeCode;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getDocumentId() {
			return documentId;
		}

		public void setDocumentId(String documentId) {
			this.documentId = documentId;
		}

		public List<JsonNodeConfig> getChildren() {
			return children;
		}

		public Integer getTypeId() {
			return typeId;
		}

		public void setTypeId(Integer typeId) {
			this.typeId = typeId;
		}

		public void setChildren(List<JsonNodeConfig> children) {
			this.children = children;
		}
	}

	public List<XmlNodeDTO> getXmlNodes() {
		return xmlNodes;
	}

	public void setXmlNodes(List<XmlNodeDTO> xmlNodes) {
		this.xmlNodes = xmlNodes;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public JsonRootType getRootType() {
		return rootType;
	}

	public void setRootType(JsonRootType rootType) {
		this.rootType = rootType;
	}

	public List<JsonNodeConfig> getJsonNodes() {
		return jsonNodes;
	}

	public void setJsonNodes(List<JsonNodeConfig> jsonNodes) {
		this.jsonNodes = jsonNodes;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public List<String> getTabList() {
		return tabList;
	}

	public void setTabList(List<String> tabList) {
		this.tabList = tabList;
	}

	public Long getOutputFormatId() {
		return outputFormatId;
	}

	public void setOutputFormatId(Long outputFormatId) {
		this.outputFormatId = outputFormatId;
	}

	public Integer getTemplateType() {
		return templateType;
	}

	public void setTemplateType(Integer templateType) {
		this.templateType = templateType;
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

	public String getWaterMark() {
		return waterMark;
	}

	public void setWaterMark(String waterMark) {
		this.waterMark = waterMark;
	}

	public String getWaterMarkOpacity() {
		return waterMarkOpacity;
	}

	public void setWaterMarkOpacity(String waterMarkOpacity) {
		this.waterMarkOpacity = waterMarkOpacity;
	}

	public Integer getPdfMargins() {
		return pdfMargins;
	}

	public void setPdfMargins(Integer pdfMargins) {
		this.pdfMargins = pdfMargins;
	}

	public CustomMargin getCustomMargin() {
		return customMargin;
	}

	public void setCustomMargin(CustomMargin customMargin) {
		this.customMargin = customMargin;
	}

	public String getTemplateBodySrc() {
		return templateBodySrc;
	}

	public void setTemplateBodySrc(String templateBodySrc) {
		this.templateBodySrc = templateBodySrc;
	}

	public List<String> getUsedAttr() {
		return usedAttr;
	}

	public void setUsedAttr(List<String> usedAttr) {
		this.usedAttr = usedAttr;
	}

	public String getXsd() {
		return xsd;
	}

	public void setXsd(String xsd) {
		this.xsd = xsd;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getSmsTempId() {
		return smsTempId;
	}

	public void setSmsTempId(String smsTempId) {
		this.smsTempId = smsTempId;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ServiceOutputFormatDTO historyOutputFormat;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ServiceOutputFormatDTO oldOutputFormat;

	public ServiceOutputFormatDTO getHistoryOutputFormat() {
		return historyOutputFormat;
	}

	public void setHistoryOutputFormat(ServiceOutputFormatDTO historyOutputFormat) {
		this.historyOutputFormat = historyOutputFormat;
		if (this.oldOutputFormat == null) {
			this.oldOutputFormat = historyOutputFormat;
		}
	}

	public ServiceOutputFormatDTO getOldOutputFormat() {
		return oldOutputFormat;
	}

	public void setOldOutputFormat(ServiceOutputFormatDTO oldOutputFormat) {
		this.oldOutputFormat = oldOutputFormat;
		if (this.historyOutputFormat == null) {
			this.historyOutputFormat = oldOutputFormat;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class CustomMargin {
		private String top;
		private String bottom;
		private String left;
		private String right;

		public String getTop() {
			return top;
		}

		public void setTop(String top) {
			this.top = top;
		}

		public String getBottom() {
			return bottom;
		}

		public void setBottom(String bottom) {
			this.bottom = bottom;
		}

		public String getLeft() {
			return left;
		}

		public void setLeft(String left) {
			this.left = left;
		}

		public String getRight() {
			return right;
		}

		public void setRight(String right) {
			this.right = right;
		}
	}
}
