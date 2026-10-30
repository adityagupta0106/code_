package com.serviceplus.metadata.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.serviceplus.metadata.departmentConfig.dto.BsaServiceMappingDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.ApplicationRouting;
import com.serviceplus.metadata.dto.ProcessFlowDTO.LimitSubmission;
import com.serviceplus.metadata.esign.dto.ServiceEsignConfigurationDTO;
import com.serviceplus.metadata.inboxSentboxFilterConfig.dto.FilterJSONDTO;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO.PatternConfig;

public class ServiceJSONDTO {
	
	private Integer serviceId;
	
	private String serviceName;
	
	private String serviceAbbrevation;
	
	private String serviceUrlHeader;
	
	private Integer stateId;
	
	private String stateName;
	
	private Long departmentId;
	
	private String departmentName;
	
	private Integer sla;
	private String slaUnit;
	
	private String applFormId;
	
	private String applSubmissionTaskId;
	
	private List<LabelValue> submissionModes;
	
	private List<LabelValue> submissionKioskTypes;

	private List<LabelValue> kioskType;

	private List<LabelValue> submissionIdentifiedUserSetIds;
	
	private List<Enclosure> enclosures;
	
	private List<PredefinedEnclosure> predefinedEnclosures;
	
	private LimitSubmission submissionLimitData;
	
	private List<ActivityMapDTO> activityMap;
	
	private List<OfficeDetailsDTO> officeDetails;
	
	private ServiceProcessFlowDTO processFlowMap;
	
	private Map<String,String> taskFormMapping;
	
	private Set<String> notificationUsedAttrList;

	private Set<String> inboxSentboxFiltersUsedAttrList;

	private Set<String> documentUsedAttrList;
	
	private Set<String> mvelUsedAttrList;

	private List<WorkFlowDataDTO> workFlowDetails;
	
	private List<MvelDetailsDTO> mvelDetailsDTO;
	
	private List<ExternalAttributeMappingDTO> externalAttributeMapping;
	
	private List<OutputFormatFreezeDTO>  outputFormatDetails;
	
	private List<WebServiceTaskDTO> webServiceTasks;
	
	private List<EscalationDetailsDTO> escalationDetails;
	
	private List<TimerTaskDTO> timerTaskDetails;

    private List<DocumentGenerationDetails> documentGenerationDetails;
    
    private List<ServiceDeliveryUnitMappingDTO> serviceDeliveryUnitMappingDetails;
    
    private List<NotificationDetails> notificationDetails;

    private Map<String, FilterJSONDTO> inboxSentboxFilters;

    private List<AuaApiConfigurationDTO> auaApiConfigurations;
    
    private Map<String, ApplicationRouting> applicationRoutingMap;
    
    private Set<String> correlationAttributeIds;

    private PaymentConfigurationDTO paymentConfiguration;

    private UsedAttributeListDTO usedAttributeList;

    private ServiceEsignConfigurationDTO esignConfiguration;

    private BsaServiceMappingDTO bsaServiceMapping;
    
    private List<PatternConfig> patternConfigurations;

    public List<PatternConfig> getPatternConfigurations() {
		return patternConfigurations;
	}

	public void setPatternConfigurations(List<PatternConfig> patternConfigurations) {
		this.patternConfigurations = patternConfigurations;
	}

	public ServiceEsignConfigurationDTO getEsignConfiguration() {
        return esignConfiguration;
    }

    public void setEsignConfiguration(ServiceEsignConfigurationDTO esignConfiguration) {
        this.esignConfiguration = esignConfiguration;
    }

    public BsaServiceMappingDTO getBsaServiceMapping() {
        return bsaServiceMapping;
    }

    public void setBsaServiceMapping(BsaServiceMappingDTO bsaServiceMapping) {
        this.bsaServiceMapping = bsaServiceMapping;
    }

    public Map<String, FilterJSONDTO> getInboxSentboxFilters() {
        return inboxSentboxFilters;
    }

    public void setInboxSentboxFilters(Map<String, FilterJSONDTO> inboxSentboxFilters) {
        this.inboxSentboxFilters = inboxSentboxFilters;
    }

    public PaymentConfigurationDTO getPaymentConfiguration() {
        return paymentConfiguration;
    }

    public void setPaymentConfiguration(PaymentConfigurationDTO paymentConfiguration) {
        this.paymentConfiguration = paymentConfiguration;
    }

    public Set<String> getCorrelationAttributeIds() {
		return correlationAttributeIds;
	}

	public void setCorrelationAttributeIds(Set<String> correlationAttributeIds) {
		this.correlationAttributeIds = correlationAttributeIds;
	}

	public Map<String, ApplicationRouting> getApplicationRoutingMap() {
        return applicationRoutingMap;
    }

    public void setApplicationRoutingMap(
            Map<String, ApplicationRouting> applicationRoutingMap) {
        this.applicationRoutingMap = applicationRoutingMap;
    }

    public Set<String> getNotificationUsedAttrList() {
		return notificationUsedAttrList;
	}

	public void setNotificationUsedAttrList(Set<String> notificationUsedAttrList) {
		this.notificationUsedAttrList = notificationUsedAttrList;
	}

	public Set<String> getInboxSentboxFiltersUsedAttrList() {
		return inboxSentboxFiltersUsedAttrList;
	}

	public void setInboxSentboxFiltersUsedAttrList(Set<String> inboxSentboxFiltersUsedAttrList) {
		this.inboxSentboxFiltersUsedAttrList = inboxSentboxFiltersUsedAttrList;
	}

	public List<NotificationDetails> getNotificationDetails() {
		return notificationDetails;
	}

	public void setNotificationDetails(List<NotificationDetails> notificationDetails) {
		this.notificationDetails = notificationDetails;
	}

	public List<ServiceDeliveryUnitMappingDTO> getServiceDeliveryUnitMappingDetails() {
		return serviceDeliveryUnitMappingDetails;
	}

	public void setServiceDeliveryUnitMappingDetails(
			List<ServiceDeliveryUnitMappingDTO> serviceDeliveryUnitMappingDetails) {
		this.serviceDeliveryUnitMappingDetails = serviceDeliveryUnitMappingDetails;
	}

	public List<DocumentGenerationDetails> getDocumentGenerationDetails() {
        return documentGenerationDetails;
    }

    public void setDocumentGenerationDetails(List<DocumentGenerationDetails> documentGenerationDetails) {
        this.documentGenerationDetails = documentGenerationDetails;
    }

	public List<EscalationDetailsDTO> getEscalationDetails() {
	    return escalationDetails;
	}

	public void setEscalationDetails(List<EscalationDetailsDTO> escalationDetails) {
	    this.escalationDetails = escalationDetails;
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

	public String getServiceAbbrevation() {
		return serviceAbbrevation;
	}

	public void setServiceAbbrevation(String serviceAbbrevation) {
		this.serviceAbbrevation = serviceAbbrevation;
	}
	
	public String getServiceUrlHeader() {
		return serviceUrlHeader;
	}

	public void setServiceUrlHeader(String serviceUrlHeader) {
		this.serviceUrlHeader = serviceUrlHeader;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Integer getSla() {
		return sla;
	}

	public void setSla(Integer sla) {
		this.sla = sla;
	}

	public String getApplFormId() {
		return applFormId;
	}

	public void setApplFormId(String applFormId) {
		this.applFormId = applFormId;
	}
	
	public String getApplSubmissionTaskId() {
		return applSubmissionTaskId;
	}

	public void setApplSubmissionTaskId(String applSubmissionTaskId) {
		this.applSubmissionTaskId = applSubmissionTaskId;
	}

	public List<LabelValue> getSubmissionModes() {
		return submissionModes;
	}

	public void setSubmissionModes(List<LabelValue> submissionModes) {
		this.submissionModes = submissionModes;
	}

	public List<Enclosure> getEnclosures() {
		return enclosures;
	}

	public void setEnclosures(List<Enclosure> enclosures) {
		this.enclosures = enclosures;
	}

	public List<PredefinedEnclosure> getPredefinedEnclosures() {
		return predefinedEnclosures;
	}

	public void setPredefinedEnclosures(List<PredefinedEnclosure> predefinedEnclosures) {
		this.predefinedEnclosures = predefinedEnclosures;
	}

	public LimitSubmission getSubmissionLimitData() {
		return submissionLimitData;
	}

	public void setSubmissionLimitData(LimitSubmission submissionLimitData) {
		this.submissionLimitData = submissionLimitData;
	}

	public List<ActivityMapDTO> getActivityMap() {
		return activityMap;
	}

	public void setActivityMap(List<ActivityMapDTO> activityMap) {
		this.activityMap = activityMap;
	}

	public List<OfficeDetailsDTO> getOfficeDetails() {
		return officeDetails;
	}

	public void setOfficeDetails(List<OfficeDetailsDTO> officeDetails) {
		this.officeDetails = officeDetails;
	}

	public ServiceProcessFlowDTO getProcessFlowMap() {
		return processFlowMap;
	}

	public void setProcessFlowMap(ServiceProcessFlowDTO processFlowMap) {
		this.processFlowMap = processFlowMap;
	}

	public Map<String, String> getTaskFormMapping() {
		return taskFormMapping;
	}

	public void setTaskFormMapping(Map<String, String> taskFormMapping) {
		this.taskFormMapping = taskFormMapping;
	}
	
	public Set<String> getDocumentUsedAttrList() {
		return documentUsedAttrList;
	}

	public void setDocumentUsedAttrList(Set<String> documentUsedAttrList) {
		this.documentUsedAttrList = documentUsedAttrList;
	}

	public Set<String> getMvelUsedAttrList() {
		return mvelUsedAttrList;
	}

	public void setMvelUsedAttrList(Set<String> mvelUsedAttrList) {
		this.mvelUsedAttrList = mvelUsedAttrList;
	}

	public List<WorkFlowDataDTO> getWorkFlowDetails() {
		return workFlowDetails;
	}
	
	public void setWorkFlowDetails(List<WorkFlowDataDTO> workFlowDetails) {
		this.workFlowDetails = workFlowDetails;
	}

	public List<MvelDetailsDTO> getMvelDetailsDTO() {
		return mvelDetailsDTO;
	}

	public void setMvelDetailsDTO(List<MvelDetailsDTO> mvelDetailsDTO) {
		this.mvelDetailsDTO = mvelDetailsDTO;
	}

	public List<ExternalAttributeMappingDTO> getExternalAttributeMapping() {
		return externalAttributeMapping;
	}

	public void setExternalAttributeMapping(List<ExternalAttributeMappingDTO> externalAttributeMapping) {
		this.externalAttributeMapping = externalAttributeMapping;
	}

	public List<OutputFormatFreezeDTO> getOutputFormatDetails() {
		return outputFormatDetails;
	}

	public void setOutputFormatDetails(List<OutputFormatFreezeDTO> outputFormatDetails) {
		this.outputFormatDetails = outputFormatDetails;
	}

	public List<WebServiceTaskDTO> getWebServiceTasks() {
		return webServiceTasks;
	}

	public void setWebServiceTasks(List<WebServiceTaskDTO> webServiceTasks) {
		this.webServiceTasks = webServiceTasks;
	}

	public List<TimerTaskDTO> getTimerTaskDetails() {
		return timerTaskDetails;
	}

	public void setTimerTaskDetails(List<TimerTaskDTO> timerTaskDetails) {
		this.timerTaskDetails = timerTaskDetails;
	}

    public List<AuaApiConfigurationDTO> getAuaApiConfigurations() {
        return auaApiConfigurations;
    }

    public void setAuaApiConfigurations(List<AuaApiConfigurationDTO> auaApiConfigurations) {
        this.auaApiConfigurations = auaApiConfigurations;
    }

    public UsedAttributeListDTO getUsedAttributeList() {
        return usedAttributeList;
    }

    public void setUsedAttributeList(UsedAttributeListDTO usedAttributeList) {
        this.usedAttributeList = usedAttributeList;
    }

	public List<LabelValue> getSubmissionKioskTypes() {
		return submissionKioskTypes;
	}

	public void setSubmissionKioskTypes(List<LabelValue> submissionKioskTypes) {
		this.submissionKioskTypes = submissionKioskTypes;
	}

	public List<LabelValue> getKioskType() {
		return kioskType != null ? kioskType : submissionKioskTypes;
	}

	public void setKioskType(List<LabelValue> kioskType) {
		this.kioskType = kioskType;
	}

    public List<LabelValue> getSubmissionIdentifiedUserSetIds() {
        return submissionIdentifiedUserSetIds;
    }

    public void setSubmissionIdentifiedUserSetIds(List<LabelValue> submissionIdentifiedUserSetIds) {
        this.submissionIdentifiedUserSetIds = submissionIdentifiedUserSetIds;
    }
    
    public String getSlaUnit() {
        return slaUnit;
    }

    public void setSlaUnit(String slaUnit) {
        this.slaUnit = slaUnit;

    }
}
