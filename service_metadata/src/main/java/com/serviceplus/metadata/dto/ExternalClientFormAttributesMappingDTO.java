package com.serviceplus.metadata.dto;

import java.io.Serializable;

public class ExternalClientFormAttributesMappingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long clientMappingId;

    private String clientId;

    private String nodeName;

    private String nodeReference;

    private String paramDescription;

    private String attributeId;

    private Boolean editable;

    private Integer serviceId;

    private String attributeInputType;

    private String attributeValue;

    private String spdvAttributeLabel;

    private Integer versionNo;

    private Integer captureAadhaar;

    private String uidMemoPath;

    private Integer fieldsetId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClientMappingId() {
		return clientMappingId;
	}

	public void setClientMappingId(Long clientMappingId) {
		this.clientMappingId = clientMappingId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeReference() {
		return nodeReference;
	}

	public void setNodeReference(String nodeReference) {
		this.nodeReference = nodeReference;
	}

	public String getParamDescription() {
		return paramDescription;
	}

	public void setParamDescription(String paramDescription) {
		this.paramDescription = paramDescription;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getAttributeInputType() {
		return attributeInputType;
	}

	public void setAttributeInputType(String attributeInputType) {
		this.attributeInputType = attributeInputType;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getSpdvAttributeLabel() {
		return spdvAttributeLabel;
	}

	public void setSpdvAttributeLabel(String spdvAttributeLabel) {
		this.spdvAttributeLabel = spdvAttributeLabel;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Integer getCaptureAadhaar() {
		return captureAadhaar;
	}

	public void setCaptureAadhaar(Integer captureAadhaar) {
		this.captureAadhaar = captureAadhaar;
	}

	public String getUidMemoPath() {
		return uidMemoPath;
	}

	public void setUidMemoPath(String uidMemoPath) {
		this.uidMemoPath = uidMemoPath;
	}

	public Integer getFieldsetId() {
		return fieldsetId;
	}

	public void setFieldsetId(Integer fieldsetId) {
		this.fieldsetId = fieldsetId;
	}
}
