package com.serviceplus.metadata.catalogue.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class ServiceCatalogueDataDTO {

	private ServiceIdentity serviceIdentity;

	private ServiceDetails serviceDetails;

	private Coverage coverage;

	private Definer definer;

	private List<Beneficiary> beneficiary;

	private Submission submission;

	private List<Deliverable> deliverables;
	private List<String> availedBy;
	private List<String> applyModes;
	private WhatYouReceive whatYouReceive;
	private Documents documents;


	public ServiceIdentity getServiceIdentity() {

		return serviceIdentity;

	}

	public void setServiceIdentity(ServiceIdentity serviceIdentity) {

		this.serviceIdentity = serviceIdentity;

	}

	public ServiceDetails getServiceDetails() {

		return serviceDetails;

	}

	public void setServiceDetails(ServiceDetails serviceDetails) {

		this.serviceDetails = serviceDetails;

	}

	@JsonIgnore

	public Coverage getCoverage() {

		return coverage;

	}

	public void setCoverage(Coverage coverage) {

		this.coverage = coverage;

	}

	@JsonIgnore

	public Definer getDefiner() {

		return definer;

	}

	public void setDefiner(Definer definer) {

		this.definer = definer;

	}

	public List<Beneficiary> getBeneficiary() {

		return beneficiary;

	}

	public void setBeneficiary(List<Beneficiary> beneficiary) {

		this.beneficiary = beneficiary;

	}

	public Submission getSubmission() {

		return submission;

	}

	public void setSubmission(Submission submission) {

		this.submission = submission;

	}

	@JsonIgnore

	public List<Deliverable> getDeliverables() {

		return deliverables;

	}

	public void setDeliverables(List<Deliverable> deliverables) {

		this.deliverables = deliverables;

	}


	public List<String> getAvailedBy() {
		return availedBy;
	}

	public void setAvailedBy(List<String> availedBy) {
		this.availedBy = availedBy;
	}

	public List<String> getApplyModes() {
		return applyModes;
	}

	public void setApplyModes(List<String> applyModes) {
		this.applyModes = applyModes;
	}

	public WhatYouReceive getWhatYouReceive() {
		return whatYouReceive;
	}

	public void setWhatYouReceive(WhatYouReceive whatYouReceive) {
		this.whatYouReceive = whatYouReceive;
	}

	public Documents getDocuments() {
		return documents;
	}

	public void setDocuments(Documents documents) {
		this.documents = documents;
	}

	public static class ServiceIdentity {

		private String tenantId;
		private Integer baseServiceId;

		private Integer serviceId;

		private String serviceName;

		private String serviceNameLocal;

		private String localLanguage;

		private Long departmentId;

		private String department;

		private String state;

		private String stateId;

		@JsonProperty("tenant_id")
		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}

		public Integer getBaseServiceId() {

			return baseServiceId;

		}

		public void setBaseServiceId(Integer baseServiceId) {

			this.baseServiceId = baseServiceId;

		}

		public Integer getServiceId() {

			return serviceId;

		}

		public void setServiceId(Integer serviceId) {

			this.serviceId = serviceId;

		}

		public String getServiceName() {

			return serviceName;

		}

		public void setServiceName(String serviceName) {

			this.serviceName = serviceName;

		}

		public String getServiceNameLocal() {

			return serviceNameLocal;

		}

		public void setServiceNameLocal(String serviceNameLocal) {

			this.serviceNameLocal = serviceNameLocal;

		}

		public String getLocalLanguage() {

			return localLanguage;

		}

		public void setLocalLanguage(String localLanguage) {

			this.localLanguage = localLanguage;

		}

		public Long getDepartmentId() {
			return departmentId;
		}

		public void setDepartmentId(Long departmentId) {
			this.departmentId = departmentId;
		}

		public String getDepartment() {

			return department;

		}

		public void setDepartment(String department) {

			this.department = department;

		}

		public String getState() {

			return state;

		}

		public void setState(String state) {

			this.state = state;

		}

		public String getStateId() {

			return stateId;

		}

		public void setStateId(String stateId) {

			this.stateId = stateId;

		}

	}

	public static class ServiceDetails {

		private String description;

		private String serviceType;

		private String serviceGroup;

		private String audience;

		private String applicantType;

		private String applyFrom;

		private String projectType;

		private String serviceUrl;

		private String alternateUrl;
		private List<String> sgdGoals;
		private HowToApply howToApply;

		public String getDescription() {

			return description;

		}

		public void setDescription(String description) {

			this.description = description;

		}

		@JsonIgnore

		public String getServiceType() {

			return serviceType;

		}

		public void setServiceType(String serviceType) {

			this.serviceType = serviceType;

		}

		@JsonIgnore

		public String getServiceGroup() {

			return serviceGroup;

		}

		public void setServiceGroup(String serviceGroup) {

			this.serviceGroup = serviceGroup;

		}

		public String getAudience() {

			return audience;

		}

		public void setAudience(String audience) {

			this.audience = audience;

		}

		public String getApplicantType() {

			return applicantType;

		}

		public void setApplicantType(String applicantType) {

			this.applicantType = applicantType;

		}

		@JsonIgnore

		public String getApplyFrom() {

			return applyFrom;

		}

		public void setApplyFrom(String applyFrom) {

			this.applyFrom = applyFrom;

		}

		@JsonIgnore

		public String getProjectType() {

			return projectType;

		}

		public void setProjectType(String projectType) {

			this.projectType = projectType;

		}

		public String getServiceUrl() {

			return serviceUrl;

		}

		public void setServiceUrl(String serviceUrl) {

			this.serviceUrl = serviceUrl;

		}

		@JsonIgnore

		public String getAlternateUrl() {

			return alternateUrl;

		}

		public void setAlternateUrl(String alternateUrl) {

			this.alternateUrl = alternateUrl;

		}

		public List<String> getSgdGoals() {
			return sgdGoals;
	}

		public void setSgdGoals(List<String> sgdGoals) {
			this.sgdGoals = sgdGoals;
	}

		public HowToApply getHowToApply() {
			return howToApply;
	}

		public void setHowToApply(HowToApply howToApply) {
			this.howToApply = howToApply;
	}

	}

	public static class Coverage {

		private String areaType;

		private String ruralCoverage;

		private String urbanCoverage;

		private String firstAcceptingUnit;

		private String firstAcceptingDepartment;

		public String getAreaType() {

			return areaType;

		}

		public void setAreaType(String areaType) {

			this.areaType = areaType;

		}

		public String getRuralCoverage() {

			return ruralCoverage;

		}

		public void setRuralCoverage(String ruralCoverage) {

			this.ruralCoverage = ruralCoverage;

		}

		public String getUrbanCoverage() {

			return urbanCoverage;

		}

		public void setUrbanCoverage(String urbanCoverage) {

			this.urbanCoverage = urbanCoverage;

		}

		public String getFirstAcceptingUnit() {

			return firstAcceptingUnit;

		}

		public void setFirstAcceptingUnit(String firstAcceptingUnit) {

			this.firstAcceptingUnit = firstAcceptingUnit;

		}

		public String getFirstAcceptingDepartment() {

			return firstAcceptingDepartment;

		}

		public void setFirstAcceptingDepartment(String firstAcceptingDepartment) {

			this.firstAcceptingDepartment = firstAcceptingDepartment;

		}

	}

	public static class Definer {

		private String name;

		private String adminName;

		private String department;

		private String contactPerson;

		private String contactNo;

		private String email;

		public String getName() {

			return name;

		}

		public void setName(String name) {

			this.name = name;

		}

		public String getAdminName() {

			return adminName;

		}

		public void setAdminName(String adminName) {

			this.adminName = adminName;

		}

		public String getDepartment() {

			return department;

		}

		public void setDepartment(String department) {

			this.department = department;

		}

		public String getContactPerson() {

			return contactPerson;

		}

		public void setContactPerson(String contactPerson) {

			this.contactPerson = contactPerson;

		}

		public String getContactNo() {

			return contactNo;

		}

		public void setContactNo(String contactNo) {

			this.contactNo = contactNo;

		}

		public String getEmail() {

			return email;

		}

		public void setEmail(String email) {

			this.email = email;

		}

	}

	public static class Beneficiary {

		private String attribute;

		private List<String> values;

		public String getAttribute() {

			return attribute;

		}

		public void setAttribute(String attribute) {

			this.attribute = attribute;

		}

		public List<String> getValues() {

			return values;

		}

		public void setValues(List<String> values) {

			this.values = values;

		}

	}

	public static class Submission {

		private List<String> modeOfSubmission;

		private String chargeAmount;

		private List<String> paymentMode;

		private String paymentUrlType;

		private String submissionType;

		private List<Enclosure> enclosures;

		@JsonIgnore

		public List<String> getModeOfSubmission() {

			return modeOfSubmission;

		}

		public void setModeOfSubmission(List<String> modeOfSubmission) {

			this.modeOfSubmission = modeOfSubmission;

		}

		@JsonIgnore

		public String getChargeAmount() {

			return chargeAmount;

		}

		public void setChargeAmount(String chargeAmount) {

			this.chargeAmount = chargeAmount;

		}

		@JsonIgnore

		public List<String> getPaymentMode() {

			return paymentMode;

		}

		public void setPaymentMode(List<String> paymentMode) {

			this.paymentMode = paymentMode;

		}

		@JsonIgnore

		public String getPaymentUrlType() {

			return paymentUrlType;

		}

		public void setPaymentUrlType(String paymentUrlType) {

			this.paymentUrlType = paymentUrlType;

		}

		public String getSubmissionType() {

			return submissionType;

		}

		public void setSubmissionType(String submissionType) {

			this.submissionType = submissionType;

		}

		@JsonIgnore

		public List<Enclosure> getEnclosures() {

			return enclosures;

		}

		public void setEnclosures(List<Enclosure> enclosures) {

			this.enclosures = enclosures;

		}

	}

	public static class Enclosure {

		private String name;

		private Boolean mandatory;

		private String copyType;

		private String attestationRequired;

		public String getName() {

			return name;

		}

		public void setName(String name) {

			this.name = name;

		}

		public Boolean getMandatory() {

			return mandatory;

		}

		public void setMandatory(Boolean mandatory) {

			this.mandatory = mandatory;

		}

		public String getCopyType() {

			return copyType;

		}

		public void setCopyType(String copyType) {

			this.copyType = copyType;

		}

		public String getAttestationRequired() {

			return attestationRequired;

		}

		public void setAttestationRequired(String attestationRequired) {

			this.attestationRequired = attestationRequired;

		}

	}

	public static class Deliverable {

		private String name;

		private Integer slaDays;

		private String deliveryMode;

		public String getName() {

			return name;

		}

		public void setName(String name) {

			this.name = name;

		}

		public Integer getSlaDays() {

			return slaDays;

		}

		public void setSlaDays(Integer slaDays) {

			this.slaDays = slaDays;

		}

		public String getDeliveryMode() {

			return deliveryMode;

		}

		public void setDeliveryMode(String deliveryMode) {

			this.deliveryMode = deliveryMode;

		}

	}

	public static class HowToApply {
		private List<String> mode;
		public List<String> getMode() { return mode; }
		public void setMode(List<String> mode) { this.mode = mode; }
	}

	public static class WhatYouReceive {
		private String deliverable;
		private List<String> deliveryModes;
		private String qrCode;
		public String getDeliverable() { return deliverable; }
		public void setDeliverable(String deliverable) { this.deliverable = deliverable; }
		public List<String> getDeliveryModes() { return deliveryModes; }
		public void setDeliveryModes(List<String> deliveryModes) { this.deliveryModes = deliveryModes; }
		public String getQrCode() { return qrCode; }
		public void setQrCode(String qrCode) { this.qrCode = qrCode; }
	}

	public static class Documents {
		private Boolean required;
		private List<DocumentItem> items;
		public Boolean getRequired() { return required; }
		public void setRequired(Boolean required) { this.required = required; }
		public List<DocumentItem> getItems() { return items; }
		public void setItems(List<DocumentItem> items) { this.items = items; }
	}

	public static class DocumentItem {
		private String name;
		private Boolean mandatory;
		private List<String> accepted;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public Boolean getMandatory() { return mandatory; }
		public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }
		public List<String> getAccepted() { return accepted; }
		public void setAccepted(List<String> accepted) { this.accepted = accepted; }
	}

}