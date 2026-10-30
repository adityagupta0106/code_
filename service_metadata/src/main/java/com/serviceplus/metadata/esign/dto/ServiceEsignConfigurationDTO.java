package com.serviceplus.metadata.esign.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.dto.LabelValue;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceEsignConfigurationDTO {

    private Long id;

    @NotNull(message = "Service ID is mandatory")
    private Integer serviceId;
    
    private Boolean esignEnabled;

    private ServiceEsignConfigurationDTO serviceEsignJson;

    // Question 1: Select eSign Template
    private Long esignTemplateId;
    
    private LabelValue esignTemplateName;

    // Question 2: Print signer name
    private Boolean printSignerName;

    // Question 3: Use biometric authentication
    private Boolean biometricEnabled;

    // Question 4: Submission modes (DEO, ONLINE, IDENTIFIED_USER_SET)
    private List<LabelValue> submissionModes;

    // Question 5: Identified User Group types (CSC, NGO_GROUP)
    private List<LabelValue> identifiedUserGroupTypes;

    // Question 6: Biometric types (FINGERPRINT, RETINA_SCANNING)
    private List<LabelValue> biometricTypes;

    // Question 7: Share Aadhaar number
    private Boolean shareAadhaar;

    // Question 8: Aadhaar task & field
    private LabelValue taskId;
    private LabelValue attributeId;

    // Question 9: Additional verification of signer's name
    private Boolean verifySignerName;

    public ServiceEsignConfigurationDTO() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Boolean getEsignEnabled() {
		return esignEnabled;
	}

	public void setEsignEnabled(Boolean esignEnabled) {
		this.esignEnabled = esignEnabled;
	}

	public ServiceEsignConfigurationDTO getServiceEsignJson() {
		return serviceEsignJson;
	}

	public void setServiceEsignJson(ServiceEsignConfigurationDTO serviceEsignJson) {
		this.serviceEsignJson = serviceEsignJson;
	}

	public Long getEsignTemplateId() {
		return esignTemplateId;
	}

	public void setEsignTemplateId(Long esignTemplateId) {
		this.esignTemplateId = esignTemplateId;
	}

	public LabelValue getEsignTemplateName() {
		return esignTemplateName;
	}

	public void setEsignTemplateName(LabelValue esignTemplateName) {
		this.esignTemplateName = esignTemplateName;
	}

	public Boolean getPrintSignerName() {
		return printSignerName;
	}

	public void setPrintSignerName(Boolean printSignerName) {
		this.printSignerName = printSignerName;
	}

	public Boolean getBiometricEnabled() {
		return biometricEnabled;
	}

	public void setBiometricEnabled(Boolean biometricEnabled) {
		this.biometricEnabled = biometricEnabled;
	}

	public List<LabelValue> getSubmissionModes() {
		return submissionModes;
	}

	public void setSubmissionModes(List<LabelValue> submissionModes) {
		this.submissionModes = submissionModes;
	}

	public List<LabelValue> getIdentifiedUserGroupTypes() {
		return identifiedUserGroupTypes;
	}

	public void setIdentifiedUserGroupTypes(List<LabelValue> identifiedUserGroupTypes) {
		this.identifiedUserGroupTypes = identifiedUserGroupTypes;
	}

	public List<LabelValue> getBiometricTypes() {
		return biometricTypes;
	}

	public void setBiometricTypes(List<LabelValue> biometricTypes) {
		this.biometricTypes = biometricTypes;
	}

	public Boolean getShareAadhaar() {
		return shareAadhaar;
	}

	public void setShareAadhaar(Boolean shareAadhaar) {
		this.shareAadhaar = shareAadhaar;
	}

	public LabelValue getTaskId() {
		return taskId;
	}

	public void setTaskId(LabelValue taskId) {
		this.taskId = taskId;
	}

	public LabelValue getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(LabelValue attributeId) {
		this.attributeId = attributeId;
	}

	public Boolean getVerifySignerName() {
		return verifySignerName;
	}

	public void setVerifySignerName(Boolean verifySignerName) {
		this.verifySignerName = verifySignerName;
	}

    
}
