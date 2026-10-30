package com.serviceplus.metadata.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serviceplus.metadata.correlationmapping.dto.ExternalServiceDataConfigurationDTO;
import com.serviceplus.metadata.document.mapping.dto.DocMappingDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

public class ProcessFlowDTO {

	private Integer serviceId;
	@OneApplicationInitiationNode
	private List<NodeDTO> nodes;
	private List<EdgeDTO> edges;
	private List<AssociatedTaskDTO> associatedTasks;
	private String tablist;

	// ----------------- Getters & Setters -----------------
	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public List<NodeDTO> getNodes() {
		return nodes;
	}

	public void setNodes(List<NodeDTO> nodes) {
		this.nodes = nodes;
	}

	public List<EdgeDTO> getEdges() {
		return edges;
	}

	public void setEdges(List<EdgeDTO> edges) {
		this.edges = edges;
	}

	public String getTablist() {
		return tablist;
	}

	public void setTablist(String tablist) {
		this.tablist = tablist;
	}

	public List<AssociatedTaskDTO> getAssociatedTasks() {
		return associatedTasks;
	}

	public void setAssociatedTasks(List<AssociatedTaskDTO> associatedTasks) {
		this.associatedTasks = associatedTasks;
	}

	@JsonIgnore
	private ProcessFlowRestrictions restrictions;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ProcessFlowDTO historyProcessFlow;

    @JsonIgnore
	private ProcessFlowDTO oldProcessFlow;

	public ProcessFlowRestrictions getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(ProcessFlowRestrictions restrictions) {
		this.restrictions = restrictions;
	}

	public ProcessFlowDTO getHistoryProcessFlow() {
		return historyProcessFlow;
	}

	public void setHistoryProcessFlow(ProcessFlowDTO historyProcessFlow) {
		this.historyProcessFlow = historyProcessFlow;
		if (this.oldProcessFlow == null) {
			this.oldProcessFlow = historyProcessFlow;
		}
	}

	public ProcessFlowDTO getOldProcessFlow() {
		return oldProcessFlow;
	}

	public void setOldProcessFlow(ProcessFlowDTO oldProcessFlow) {
		this.oldProcessFlow = oldProcessFlow;
		if (this.historyProcessFlow == null) {
			this.historyProcessFlow = oldProcessFlow;
		}
	}

	// ----------------- Minor Version Restrictions -----------------
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ProcessFlowRestrictions {
		private boolean isMinorVersion;
		private boolean allowDeleteTask;
		private boolean allowChangeTaskType;
		private boolean allowDeleteSDU;
		private boolean allowChangeFormMapping;
		private boolean allowChangeBunchingRules;
		private boolean allowModifyDbtTasks;
		private List<String> restrictedTaskIds;
		private List<String> messages;
		private String modificationToken;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private ProcessFlowDTO historyProcessFlow;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private ProcessFlowDTO oldProcessFlow;

		public boolean isMinorVersion() {
			return isMinorVersion;
		}

		public void setMinorVersion(boolean isMinorVersion) {
			this.isMinorVersion = isMinorVersion;
		}

		public boolean isAllowDeleteTask() {
			return allowDeleteTask;
		}

		public void setAllowDeleteTask(boolean allowDeleteTask) {
			this.allowDeleteTask = allowDeleteTask;
		}

		public boolean isAllowChangeTaskType() {
			return allowChangeTaskType;
		}

		public void setAllowChangeTaskType(boolean allowChangeTaskType) {
			this.allowChangeTaskType = allowChangeTaskType;
		}

		public boolean isAllowDeleteSDU() {
			return allowDeleteSDU;
		}

		public void setAllowDeleteSDU(boolean allowDeleteSDU) {
			this.allowDeleteSDU = allowDeleteSDU;
		}

		public boolean isAllowChangeFormMapping() {
			return allowChangeFormMapping;
		}

		public void setAllowChangeFormMapping(boolean allowChangeFormMapping) {
			this.allowChangeFormMapping = allowChangeFormMapping;
		}

		public boolean isAllowChangeBunchingRules() {
			return allowChangeBunchingRules;
		}

		public void setAllowChangeBunchingRules(boolean allowChangeBunchingRules) {
			this.allowChangeBunchingRules = allowChangeBunchingRules;
		}

		public boolean isAllowModifyDbtTasks() {
			return allowModifyDbtTasks;
		}

		public void setAllowModifyDbtTasks(boolean allowModifyDbtTasks) {
			this.allowModifyDbtTasks = allowModifyDbtTasks;
		}

		public List<String> getRestrictedTaskIds() {
			return restrictedTaskIds;
		}

		public void setRestrictedTaskIds(List<String> restrictedTaskIds) {
			this.restrictedTaskIds = restrictedTaskIds;
		}

		public List<String> getMessages() {
			return messages;
		}

		public void setMessages(List<String> messages) {
			this.messages = messages;
		}

		public String getModificationToken() {
			return modificationToken;
		}

		public void setModificationToken(String modificationToken) {
			this.modificationToken = modificationToken;
		}

		public ProcessFlowDTO getHistoryProcessFlow() {
			return historyProcessFlow;
		}

		public void setHistoryProcessFlow(ProcessFlowDTO historyProcessFlow) {
			this.historyProcessFlow = historyProcessFlow;
			if (this.oldProcessFlow == null) {
				this.oldProcessFlow = historyProcessFlow;
			}
		}

		public ProcessFlowDTO getOldProcessFlow() {
			return oldProcessFlow;
		}

		public void setOldProcessFlow(ProcessFlowDTO oldProcessFlow) {
			this.oldProcessFlow = oldProcessFlow;
			if (this.historyProcessFlow == null) {
				this.historyProcessFlow = oldProcessFlow;
			}
		}
	}

	@Constraint(validatedBy = OneApplicationInitiationNodeValidator.class)
	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface OneApplicationInitiationNode {
		String message() default "Exactly one node must be of type 'submission' with name 'Service Initiation'";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

	public static class OneApplicationInitiationNodeValidator
			implements ConstraintValidator<OneApplicationInitiationNode, List<NodeDTO>> {
		@Override
		public void initialize(OneApplicationInitiationNode constraintAnnotation) {
		}

		@Override
		public boolean isValid(List<NodeDTO> nodes, ConstraintValidatorContext context) {
			if (nodes == null) {
				return false;
			}
			long count = nodes.stream()
					.filter(node -> node != null && ApplicationConstants.SUBMISSION_NODE_TYPE.equals(node.getType())
							&& node.getData() != null
							&& ApplicationConstants.SUBMISSION_NODE_NAME.equals(node.getData().getName()))
					.count();
			return count == 1;
		}
	}

	// ----------------- Node DTO -----------------
	public static class NodeDTO {
		private String id;
		private String type;
		private Position position;
		private DataDTO data;
		private Measured measured;
		private boolean selected;
		private boolean dragging;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Position getPosition() {
			return position;
		}

		public void setPosition(Position position) {
			this.position = position;
		}

		public DataDTO getData() {
			return data;
		}

		public void setData(DataDTO data) {
			this.data = data;
		}

		public Measured getMeasured() {
			return measured;
		}

		public void setMeasured(Measured measured) {
			this.measured = measured;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public boolean isDragging() {
			return dragging;
		}

		public void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		public static class Position {
			private double x;
			private double y;

			public double getX() {
				return x;
			}

			public void setX(double x) {
				this.x = x;
			}

			public double getY() {
				return y;
			}

			public void setY(double y) {
				this.y = y;
			}
		}

		public static class Measured {
			private double width;
			private double height;

			public double getWidth() {
				return width;
			}

			public void setWidth(double width) {
				this.width = width;
			}

			public double getHeight() {
				return height;
			}

			public void setHeight(double height) {
				this.height = height;
			}
		}
	}

	// ----------------- Data DTO (merged fields) -----------------
	public static class DataDTO {
		// --- Submission fields ---
		private List<LabelValue> mode;
		private List<LabelValue> kioskType;
		private List<LabelValue> identifiedUserSetIds;
		private String enclosuresRequired;
		private List<Enclosure> enclosures;
		private List<PredefinedEnclosure> predefined;// need to add
		private LabelValue edgeType;
		private AutoDelete autodelete;
		private LimitSubmission limitSubmission;
		private Map<String,Action> workflow;
		private boolean deoSubmissionContainedWithinLocation;
		private FormDetail formDetail;
		private EditForm editPermission;
		
		private ExternalServiceDataConfigurationDTO externalDataSet;

		//--- Document Mapping ---
		private List<DocMappingDTO> documentMapping;		
		//--- Payment ---
		private TaskPaymentMappingDTO payment;
		
		// --- Task fields ---
		private String name;
		private String description;
		private LabelValue taskType;
		private LabelValue humanTask;
		private LabelValue systemTask;
		private LabelValue templateTask;
		private List<Entities> entities;
		private Advanced advanced;
		private WebserviceDetails webserviceDetails;
		private List<ExternalSystemDetail> externalSystemDetail;
		private Escalation escalation;
		private ApplicationRouting applicationRouting;
		
		// --- Gateway fields ---
		private LabelValue behaviour;
		private List<AssociatedTaskReferenceDTO> associatedTasks;

		
		// ----------------- Getters & Setters -----------------
		public void setMode(List<LabelValue> mode) {
			this.mode = mode;
		}

		public List<LabelValue> getKioskType() {
			return kioskType;
		}

		public void setKioskType(List<LabelValue> kioskType) {
			this.kioskType = kioskType;
		}

        public List<LabelValue> getIdentifiedUserSetIds() {
            return identifiedUserSetIds;
        }

        public void setIdentifiedUserSetIds(List<LabelValue> identifiedUserSetIds) {
            this.identifiedUserSetIds = identifiedUserSetIds;
        }

        public String getEnclosuresRequired() {
			return enclosuresRequired;
		}

		public void setEnclosuresRequired(String enclosuresRequired) {
			this.enclosuresRequired = enclosuresRequired;
		}

		public List<Enclosure> getEnclosures() {
			return enclosures;
		}

		public void setEnclosures(List<Enclosure> enclosures) {
			this.enclosures = enclosures;
		}

		public List<PredefinedEnclosure> getPredefined() {
			return predefined;
		}

		public void setPredefined(List<PredefinedEnclosure> predefined) {
			this.predefined = predefined;
		}

		public LabelValue getEdgeType() {
			return edgeType;
		}

		public void setEdgeType(LabelValue edgeType) {
			this.edgeType = edgeType;
		}

		public AutoDelete getAutodelete() {
			return autodelete;
		}

		public void setAutodelete(AutoDelete autodelete) {
			this.autodelete = autodelete;
		}

		public LimitSubmission getLimitSubmission() {
			return limitSubmission;
		}

		public void setLimitSubmission(LimitSubmission limitSubmission) {
			this.limitSubmission = limitSubmission;
		}

		public boolean isDeoSubmissionContainedWithinLocation() {
			return deoSubmissionContainedWithinLocation;
		}

		public void setDeoSubmissionContainedWithinLocation(boolean deoSubmissionContainedWithinLocation) {
			this.deoSubmissionContainedWithinLocation = deoSubmissionContainedWithinLocation;
		}

		public ExternalServiceDataConfigurationDTO getExternalDataSet() {
			return externalDataSet;
		}

		public void setExternalDataSet(ExternalServiceDataConfigurationDTO externalDataSet) {
			this.externalDataSet = externalDataSet;
		}

		public FormDetail getFormDetail() {
			return formDetail;
		}

		public void setFormDetail(FormDetail formDetail) {
			this.formDetail = formDetail;
		}

		public EditForm getEditPermission() {
			return editPermission;
		}

		public void setEditPermission(EditForm editPermission) {
			this.editPermission = editPermission;
		}

		public List<DocMappingDTO> getDocumentMapping() {
			return documentMapping;
		}

		public void setDocumentMapping(List<DocMappingDTO> documentMapping) {
			this.documentMapping = documentMapping;
		}
		
		public TaskPaymentMappingDTO getPayment() {
			return payment;
		}

		public void setPayment(TaskPaymentMappingDTO payment) {
			this.payment = payment;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public LabelValue getTaskType() {
			return taskType;
		}

		public void setTaskType(LabelValue taskType) {
			this.taskType = taskType;
		}

		public LabelValue getHumanTask() {
			return humanTask;
		}

		public void setHumanTask(LabelValue humanTask) {
			this.humanTask = humanTask;
		}

		public LabelValue getSystemTask() {
			return systemTask;
		}

		public void setSystemTask(LabelValue systemTask) {
			this.systemTask = systemTask;
		}

		public LabelValue getTemplateTask() {
			return templateTask;
		}

		public void setTemplateTask(LabelValue templateTask) {
			this.templateTask = templateTask;
		}

		public List<Entities> getEntities() {
			return entities;
		}

		public void setEntities(List<Entities> entities) {
			this.entities = entities;
		}

		public Advanced getAdvanced() {
			return advanced;
		}

		public void setAdvanced(Advanced advanced) {
			this.advanced = advanced;
		}

		public WebserviceDetails getWebserviceDetails() {
			return webserviceDetails;
		}

		public void setWebserviceDetails(WebserviceDetails webserviceDetails) {
			this.webserviceDetails = webserviceDetails;
		}
		
		public List<ExternalSystemDetail> getExternalSystemDetail() {
			return externalSystemDetail;
		}

		public void setExternalSystemDetail(List<ExternalSystemDetail> externalSystemDetail) {
			this.externalSystemDetail = externalSystemDetail;
		}
		
		public List<LabelValue> getMode() {
			return mode;
		}

		public LabelValue getBehaviour() {
			return behaviour;
		}

		public void setBehaviour(LabelValue behaviour) {
			this.behaviour = behaviour;
		}
		
		public Map<String, Action> getWorkflow() {
			return workflow;
		}

		public void setWorkflow(Map<String, Action> workflow) {
			this.workflow = workflow;
		}

		public Escalation getEscalation() {
			return escalation;
		}

		public void setEscalation(Escalation escalation) {
			this.escalation = escalation;
		}

		public ApplicationRouting getApplicationRouting() {
			return applicationRouting;
		}

		public void setApplicationRouting(ApplicationRouting applicationRouting) {
			this.applicationRouting = applicationRouting;
		}

		public List<AssociatedTaskReferenceDTO> getAssociatedTasks() {
		    return associatedTasks;
		}

		public void setAssociatedTasks(List<AssociatedTaskReferenceDTO> associatedTasks) {
		    this.associatedTasks = associatedTasks;
		}

		public static class AssociatedTaskReferenceDTO {

		    private String associatedTaskId;

		    private List<String> triggerPoint;

		    private List<String> triggerOnAction;

		    private List<String> executionTasks;

		    private Boolean isUnsubscribed;

		    private FormDetail formDetail;

		    private WebserviceDetails.Definition apiDefinition;

		    public String getAssociatedTaskId() {
		        return associatedTaskId;
		    }

		    public void setAssociatedTaskId(String associatedTaskId) {
		        this.associatedTaskId = associatedTaskId;
		    }

		    public List<String> getTriggerPoint() {
		        return triggerPoint;
		    }

		    public void setTriggerPoint(List<String> triggerPoint) {
		        this.triggerPoint = triggerPoint;
		    }

		    public List<String> getTriggerOnAction() {
		        return triggerOnAction;
		    }

		    public void setTriggerOnAction(List<String> triggerOnAction) {
		        this.triggerOnAction = triggerOnAction;
		    }

		    public List<String> getExecutionTasks() {
		        return executionTasks;
		    }

		    public void setExecutionTasks(List<String> executionTasks) {
		        this.executionTasks = executionTasks;
		    }

		    public Boolean getIsUnsubscribed() {
		        return isUnsubscribed;
		    }

		    public void setIsUnsubscribed(Boolean isUnsubscribed) {
		        this.isUnsubscribed = isUnsubscribed;
		    }

		    public FormDetail getFormDetail() {
		        return formDetail;
		    }

		    public void setFormDetail(FormDetail formDetail) {
		        this.formDetail = formDetail;
		    }

		    public WebserviceDetails.Definition getApiDefinition() {
		        return apiDefinition;
		    }

		    public void setApiDefinition(WebserviceDetails.Definition apiDefinition) {
		        this.apiDefinition = apiDefinition;
		    }
		}

		public static class Escalation {

		    private Boolean enabled;
		    private TimePeriod slaPeriod;
		    private TimePeriod escalationPeriod;
		    private List<LabelValue> defaultActions;
		    private String mvelExpression;

		    public Boolean getEnabled() {
		        return enabled;
		    }

		    public void setEnabled(Boolean enabled) {
		        this.enabled = enabled;
		    }

		    public TimePeriod getSlaPeriod() {
		        return slaPeriod;
		    }

		    public void setSlaPeriod(TimePeriod slaPeriod) {
		        this.slaPeriod = slaPeriod;
		    }

		    public TimePeriod getEscalationPeriod() {
		        return escalationPeriod;
		    }

		    public void setEscalationPeriod(TimePeriod escalationPeriod) {
		        this.escalationPeriod = escalationPeriod;
		    }

		    public List<LabelValue> getDefaultActions() {
		        return defaultActions;
		    }

		    public void setDefaultActions(List<LabelValue> defaultActions) {
		        this.defaultActions = defaultActions;
		    }

		    public String getMvelExpression() {
		        return mvelExpression;
		    }

		    public void setMvelExpression(String mvelExpression) {
		        this.mvelExpression = mvelExpression;
		    }
		}
		
		public static class Action {
			private Boolean enabled;
			private String actionFormLabel;
			private String historyLabel;
			private String trackLabel;
			private Boolean logicalClosure;
			private Boolean completeClosure;
			private Remarks remarks;
			private Boolean isSubscribed;

			public Boolean getEnabled() {
				return enabled;
			}

			public void setEnabled(Boolean enabled) {
				this.enabled = enabled;
			}

			public String getActionFormLabel() {
				return actionFormLabel;
			}

			public void setActionFormLabel(String actionFormLabel) {
				this.actionFormLabel = actionFormLabel;
			}

			public String getHistoryLabel() {
				return historyLabel;
			}

			public void setHistoryLabel(String historyLabel) {
				this.historyLabel = historyLabel;
			}

			public String getTrackLabel() {
				return trackLabel;
			}

			public void setTrackLabel(String trackLabel) {
				this.trackLabel = trackLabel;
			}

			public Boolean getLogicalClosure() {
				return logicalClosure;
			}

			public void setLogicalClosure(Boolean logicalClosure) {
				this.logicalClosure = logicalClosure;
			}

			public Boolean getCompleteClosure() {
				return completeClosure;
			}

			public void setCompleteClosure(Boolean completeClosure) {
				this.completeClosure = completeClosure;
			}

			public Remarks getRemarks() {
				return remarks;
			}

			public void setRemarks(Remarks remarks) {
				this.remarks = remarks;
			}
			public Boolean getIsSubscribed() {
				return isSubscribed;
			}
			
			public void setIsSubscribed(Boolean isSubscribed) {
				this.isSubscribed = isSubscribed;
			}
		}

		public static class Remarks {
			private Boolean applicant;
			private Boolean official;
			private Boolean mandatory;

			public Boolean getApplicant() {
				return applicant;
			}

			public void setApplicant(Boolean applicant) {
				this.applicant = applicant;
			}

			public Boolean getOfficial() {
				return official;
			}

			public void setOfficial(Boolean official) {
				this.official = official;
			}

			public Boolean getMandatory() {
				return mandatory;
			}

			public void setMandatory(Boolean mandatory) {
				this.mandatory = mandatory;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;
				Remarks remarks = (Remarks) o;
				return Objects.equals(applicant, remarks.applicant) &&
						Objects.equals(official, remarks.official) &&
						Objects.equals(mandatory, remarks.mandatory);
			}

			@Override
			public int hashCode() {
				return Objects.hash(applicant, official, mandatory);
			}

			@Override
			public String toString() {
				return "applicant=" + Boolean.TRUE.equals(applicant) +
						", official=" + Boolean.TRUE.equals(official) +
						", mandatory=" + Boolean.TRUE.equals(mandatory);
			}
		}

	}
	
	public static class ApplicationRouting {

	    private Boolean enabled;

	    private String routingMode;

	    private List<RoutingAttribute> selectedAttributes;

	    private List<RoutingCombination> combinations;

	    public Boolean getEnabled() {
	        return enabled;
	    }

	    public void setEnabled(Boolean enabled) {
	        this.enabled = enabled;
	    }

	    public String getRoutingMode() {
	        return routingMode;
	    }

	    public void setRoutingMode(String routingMode) {
	        this.routingMode = routingMode;
	    }

	    public List<RoutingAttribute> getSelectedAttributes() {
	        return selectedAttributes;
	    }

	    public void setSelectedAttributes(List<RoutingAttribute> selectedAttributes) {
	        this.selectedAttributes = selectedAttributes;
	    }

	    public List<RoutingCombination> getCombinations() {
	        return combinations;
	    }

	    public void setCombinations(List<RoutingCombination> combinations) {
	        this.combinations = combinations;
	    }
	}
	
	public static class RoutingAttribute {
		
	    private String taskId;

	    private String attributeType;

	    private String attributeId;

	    private String attributeKey;

	    private String attributeName;

	    private Integer priority;

	    private Integer hierarchyLevel;

	    public String getTaskId() {
	        return taskId;
	    }

	    public void setTaskId(String taskId) {
	        this.taskId = taskId;
	    }

	    public String getAttributeType() {
	        return attributeType;
	    }

	    public void setAttributeType(String attributeType) {
	        this.attributeType = attributeType;
	    }

	    public String getAttributeId() {
	        return attributeId;
	    }

	    public void setAttributeId(String attributeId) {
	        this.attributeId = attributeId;
	    }

	    public String getAttributeKey() {
	        return attributeKey;
	    }

	    public void setAttributeKey(String attributeKey) {
	        this.attributeKey = attributeKey;
	    }

	    public String getAttributeName() {
	        return attributeName;
	    }

	    public void setAttributeName(String attributeName) {
	        this.attributeName = attributeName;
	    }

	    public Integer getPriority() {
	        return priority;
	    }

	    public void setPriority(Integer priority) {
	        this.priority = priority;
	    }

	    public Integer getHierarchyLevel() {
	        return hierarchyLevel;
	    }

	    public void setHierarchyLevel(Integer hierarchyLevel) {
	        this.hierarchyLevel = hierarchyLevel;
	    }
	}
	
	public static class RoutingCombination {

	    private Integer priority;

	    private List<String> sourceAttributeIds;
	    
	    private List<String> destinationAttributeIds;

	    public Integer getPriority() {
	        return priority;
	    }

	    public void setPriority(Integer priority) {
	        this.priority = priority;
	    }

		public List<String> getSourceAttributeIds() {
			return sourceAttributeIds;
		}

		public void setSourceAttributeIds(List<String> sourceAttributeIds) {
			this.sourceAttributeIds = sourceAttributeIds;
		}

		public List<String> getDestinationAttributeIds() {
			return destinationAttributeIds;
		}

		public void setDestinationAttributeIds(List<String> destinationAttributeIds) {
			this.destinationAttributeIds = destinationAttributeIds;
		}

	}

	public static class Behaviour {
		private boolean behaviour;

		public boolean isBehaviour() {
			return behaviour;
		}

		public void setBehaviour(boolean behaviour) {
			this.behaviour = behaviour;
		}
	}

	// ----------------- Form Detail -----------------
	public static class FormDetail {
		private String formId;
		private String holderId;
		private Boolean isSubscribed;
		private String subscriptionMode;
		private String parentForm;
		private String templateName;
		private Boolean defaultFormEncryptionRequired;
		private Boolean encryptDataInTransit;
		private Boolean encryptDataAtRest;
		private Boolean standalone;
        @JsonProperty("isGlobal")
        private Boolean isGlobal;

        @JsonProperty("isGlobal")
        public Boolean getGlobal() {
            return isGlobal;
        }

        @JsonProperty("isGlobal")
        public void setGlobal(Boolean global) {
            isGlobal = global;
        }

        public String getFormId() {
			return formId;
		}

		public void setFormId(String formId) {
			this.formId = formId;
		}

		public String getHolderId() {
			return holderId;
		}

		public void setHolderId(String holderId) {
			this.holderId = holderId;
		}

		public Boolean getIsSubscribed() {
			return isSubscribed;
		}

		public void setIsSubscribed(Boolean isSubscribed) {
			this.isSubscribed = isSubscribed;
		}

		public String getSubscriptionMode() {
			return subscriptionMode;
		}

		public void setSubscriptionMode(String subscriptionMode) {
			this.subscriptionMode = subscriptionMode;
		}

		public String getParentForm() {
			return parentForm;
		}

		public void setParentForm(String parentForm) {
			this.parentForm = parentForm;
		}

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public Boolean getDefaultFormEncryptionRequired() {
			return defaultFormEncryptionRequired;
		}

		public void setDefaultFormEncryptionRequired(Boolean defaultFormEncryptionRequired) {
			this.defaultFormEncryptionRequired = defaultFormEncryptionRequired;
		}

		public Boolean getEncryptDataInTransit() {
			return encryptDataInTransit;
		}

		public void setEncryptDataInTransit(Boolean encryptDataInTransit) {
			this.encryptDataInTransit = encryptDataInTransit;
		}

		public Boolean getEncryptDataAtRest() {
			return encryptDataAtRest;
		}

		public void setEncryptDataAtRest(Boolean encryptDataAtRest) {
			this.encryptDataAtRest = encryptDataAtRest;
		}

		public Boolean getStandalone() {
			return standalone;
		}

		public void setStandalone(Boolean standalone) {
			this.standalone = standalone;
		}

        public Boolean getSubscribed() {
            return isSubscribed;
        }

        public void setSubscribed(Boolean subscribed) {
            isSubscribed = subscribed;
        }

		public Boolean getIsGlobal() {
			return isGlobal;
		}

		public void setIsGlobal(Boolean isGlobal) {
			this.isGlobal = isGlobal;
		}
        
    }

	// Edit Form
	public static class EditForm {

		private boolean value;
		private LabelValue placement;
		private List<EditFormData> data;

		public enum EditControl {
			COMPLETE_FORM(1, "Edit complete form"), SPECIFIC_FIELDS(2, "Edit specific fields");

			private final int value;
			private final String label;

			EditControl(int value, String label) {
				this.value = value;
				this.label = label;
			}

			public int getValue() {
				return value;
			}

			public String getLabel() {
				return label;
			}
		}

		public static class EditFormData {

			private LabelValue selectedForm;
			private String selectedHolderId;
			private LabelValue control;
			private Specific specific;
			private Integer sortOrder;
			private LabelValue modifyChoice;

			public static class Specific {
				private List<LabelValue> allowed;
				private List<LabelValue> restricted;
				private List<LabelValue> hide;

				public List<LabelValue> getAllowed() {
					return allowed;
				}

				public void setAllowed(List<LabelValue> allowed) {
					this.allowed = allowed;
				}

				public List<LabelValue> getRestricted() {
					return restricted;
				}

				public void setRestricted(List<LabelValue> restricted) {
					this.restricted = restricted;
				}

				public List<LabelValue> getHide() {
					return hide;
				}

				public void setHide(List<LabelValue> hide) {
					this.hide = hide;
				}

			}

			public LabelValue getSelectedForm() {
				return selectedForm;
			}

			public void setSelectedForm(LabelValue selectedForm) {
				this.selectedForm = selectedForm;
			}

			public Integer getSortOrder() {
				return sortOrder;
			}

			public void setSortOrder(Integer sortOrder) {
				this.sortOrder = sortOrder;
			}

			public LabelValue getControl() {
				return control;
			}

			public void setControl(LabelValue control) {
				this.control = control;
			}

			public LabelValue getModifyChoice() {
				return modifyChoice;
			}

			public void setModifyChoice(LabelValue modifyChoice) {
				this.modifyChoice = modifyChoice;
			}

			public Specific getSpecific() {
				return specific;
			}

			public void setSpecific(Specific specific) {
				this.specific = specific;
			}

			public String getSelectedHolderId() {
				return selectedHolderId;
			}

			public void setSelectedHolderId(String selectedHolderId) {
				this.selectedHolderId = selectedHolderId;
			}

		}

		public boolean getValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public List<EditFormData> getData() {
			return data;
		}

		public void setData(List<EditFormData> data) {
			this.data = data;
		}

		public LabelValue getPlacement() {
			return placement;
		}

		public void setPlacement(LabelValue placement) {
			this.placement = placement;
		}

	}
	// ----------------- Department -----------------
	public static class Entities {
		private String type;
		private LabelValue entity;
		private LabelValue category;
		private LabelValue clcDetail;
		private LabelValue entityLevel;
		private List<Designation> designations;
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public LabelValue getEntity() {
			return entity;
		}
		public void setEntity(LabelValue entity) {
			this.entity = entity;
		}
		public LabelValue getClcDetail() {
			return clcDetail;
		}
		public void setClcDetail(LabelValue clcDetail) {
			this.clcDetail = clcDetail;
		}
		public LabelValue getEntityLevel() {
			return entityLevel;
		}
		public void setEntityLevel(LabelValue entityLevel) {
			this.entityLevel = entityLevel;
		}
		public LabelValue getCategory() {
			return category;
		}
		public void setCategory(LabelValue category) {
			this.category = category;
		}
		public List<Designation> getDesignations() {
			return designations;
		}
		public void setDesignations(List<Designation> designations) {
			this.designations = designations;
		}
		
	}

	public static class Designation {
		private LabelValue designation;

		public LabelValue getDesignation() {
			return designation;
		}

		public void setDesignation(LabelValue designation) {
			this.designation = designation;
		}
	}
	
	// ----------------- Advanced -----------------
	public static class Advanced {
		private Applicant applicant;
		private Official official;
		private MultiProcessing multiProcessing;
		private DisplayUserTask displayUserTask;
		private ServiceLevel serviceLevel;
		private Timer timer;
		private DBT dbt;

		public static class DBT {
			private String type;
            private String xmlGenerationTask;
            private String xmlTemplate;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getXmlGenerationTask() {
                return xmlGenerationTask;
            }

            public void setXmlGenerationTask(String xmlGenerationTask) {
                this.xmlGenerationTask = xmlGenerationTask;
            }

            public String getXmlTemplate() {
                return xmlTemplate;
            }

            public void setXmlTemplate(String xmlTemplate) {
                this.xmlTemplate = xmlTemplate;
            }
        }
		
		public DBT getDbt() {
			return dbt;
		}

		public void setDbt(DBT dbt) {
			this.dbt = dbt;
		}

		public Applicant getApplicant() {
			return applicant;
		}

		public void setApplicant(Applicant applicant) {
			this.applicant = applicant;
		}

		public Official getOfficial() {
			return official;
		}

		public void setOfficial(Official official) {
			this.official = official;
		}

		public MultiProcessing getMultiProcessing() {
			return multiProcessing;
		}

		public void setMultiProcessing(MultiProcessing multiProcessing) {
			this.multiProcessing = multiProcessing;
		}

		public DisplayUserTask getDisplayUserTask() {
			return displayUserTask;
		}

		public void setDisplayUserTask(DisplayUserTask displayUserTask) {
			this.displayUserTask = displayUserTask;
		}

		public ServiceLevel getServiceLevel() {
			return serviceLevel;
		}

		public void setServiceLevel(ServiceLevel serviceLevel) {
			this.serviceLevel = serviceLevel;
		}

		public Timer getTimer() {
			return timer;
		}

		public void setTimer(Timer timer) {
			this.timer = timer;
		}
	}
	
	public static class Timer {

	    private Boolean showInTrack;
	    private MultiProcessing multiProcessing;
	    private PredefinedProcessing predefinedProcessing;
	    private TaskExecutionPeriod taskExecustionPeriod;

	    public Boolean getShowInTrack() {
	        return showInTrack;
	    }

	    public void setShowInTrack(Boolean showInTrack) {
	        this.showInTrack = showInTrack;
	    }

	    public MultiProcessing getMultiProcessing() {
	        return multiProcessing;
	    }

	    public void setMultiProcessing(MultiProcessing multiProcessing) {
	        this.multiProcessing = multiProcessing;
	    }

	    public PredefinedProcessing getPredefinedProcessing() {
	        return predefinedProcessing;
	    }

	    public void setPredefinedProcessing(PredefinedProcessing predefinedProcessing) {
	        this.predefinedProcessing = predefinedProcessing;
	    }

	    public TaskExecutionPeriod getTaskExecustionPeriod() {
	        return taskExecustionPeriod;
	    }

	    public void setTaskExecustionPeriod(TaskExecutionPeriod taskExecustionPeriod) {
	        this.taskExecustionPeriod = taskExecustionPeriod;
	    }
	}
	
	public static class TaskExecutionPeriod {
		
		@JsonProperty("static")
	    private Static staticValue;
	    private LabelValue behaviour;

	    public Static getStaticValue() {
	        return staticValue;
	    }

	    public void setStaticValue(Static staticValue) {
	        this.staticValue = staticValue;
	    }

		public LabelValue getBehaviour() {
			return behaviour;
		}

		public void setBehaviour(LabelValue behaviour) {
			this.behaviour = behaviour;
		}
	}
	public static class Static {

	    private Unit unit;
	    private Integer taskExecutionPeriod;

	    public Unit getUnit() {
	        return unit;
	    }

	    public void setUnit(Unit unit) {
	        this.unit = unit;
	    }

	    public Integer getTaskExecutionPeriod() {
	        return taskExecutionPeriod;
	    }

	    public void setTaskExecutionPeriod(Integer taskExecutionPeriod) {
	        this.taskExecutionPeriod = taskExecutionPeriod;
	    }
	}
	
	public static class Unit {

	    private String label;
	    private Integer value;

	    public String getLabel() {
	        return label;
	    }

	    public void setLabel(String label) {
	        this.label = label;
	    }

	    public Integer getValue() {
	        return value;
	    }

	    public void setValue(Integer value) {
	        this.value = value;
	    }
	}
	

	public static class Applicant {
		private boolean deoSubmit;
        private boolean uploadRejectedEnclosures;
        private boolean submissionToSameOfficial;

		public boolean isDeoSubmit() {
			return deoSubmit;
		}

		public void setDeoSubmit(boolean deoSubmit) {
			this.deoSubmit = deoSubmit;
		}

        public boolean isUploadRejectedEnclosures() {
            return uploadRejectedEnclosures;
        }

        public void setUploadRejectedEnclosures(boolean uploadRejectedEnclosures) {
            this.uploadRejectedEnclosures = uploadRejectedEnclosures;
        }

        public boolean isSubmissionToSameOfficial() {
            return submissionToSameOfficial;
        }

        public void setSubmissionToSameOfficial(boolean submissionToSameOfficial) {
            this.submissionToSameOfficial = submissionToSameOfficial;
        }
    }

	public static class Official {
		private boolean additionalEnclosures;
		private boolean rejectEnclosure;
		private boolean conditionalCallback;
		private boolean priority;
		private boolean override;
		private boolean print;

		private boolean reassign;
		private boolean escalate;
		private boolean verify;
		private boolean certificate;
		private boolean allowRevision;

		private MultiProcessing multiProcessing;
		private ApplicantEditPermission applicantEditPermission;
		private PredefinedProcessing predefinedProcessing;

		public boolean isAdditionalEnclosures() {
			return additionalEnclosures;
		}

		public void setAdditionalEnclosures(boolean additionalEnclosures) {
			this.additionalEnclosures = additionalEnclosures;
		}

		public boolean isRejectEnclosure() {
			return rejectEnclosure;
		}

		public void setRejectEnclosure(boolean rejectEnclosure) {
			this.rejectEnclosure = rejectEnclosure;
		}

		public boolean isConditionalCallback() {
			return conditionalCallback;
		}

		public void setConditionalCallback(boolean conditionalCallback) {
			this.conditionalCallback = conditionalCallback;
		}

		public boolean isPriority() {
			return priority;
		}

		public void setPriority(boolean priority) {
			this.priority = priority;
		}

		public boolean isOverride() {
			return override;
		}

		public void setOverride(boolean override) {
			this.override = override;
		}

		public boolean isPrint() {
			return print;
		}

		public void setPrint(boolean print) {
			this.print = print;
		}

		public boolean isReassign() {
			return reassign;
		}

		public void setReassign(boolean reassign) {
			this.reassign = reassign;
		}

		public boolean isEscalate() {
			return escalate;
		}

		public void setEscalate(boolean escalate) {
			this.escalate = escalate;
		}

		public boolean isVerify() {
			return verify;
		}

		public void setVerify(boolean verify) {
			this.verify = verify;
		}

		public boolean isCertificate() {
			return certificate;
		}

		public void setCertificate(boolean certificate) {
			this.certificate = certificate;
		}

		public boolean isAllowRevision() {
			return allowRevision;
		}

		public void setAllowRevision(boolean allowRevision) {
			this.allowRevision = allowRevision;
		}

		public MultiProcessing getMultiProcessing() {
			return multiProcessing;
		}

		public void setMultiProcessing(MultiProcessing multiProcessing) {
			this.multiProcessing = multiProcessing;
		}

		public ApplicantEditPermission getApplicantEditPermission() {
			return applicantEditPermission;
		}

		public void setApplicantEditPermission(ApplicantEditPermission applicantEditPermission) {
			this.applicantEditPermission = applicantEditPermission;
		}

		public PredefinedProcessing getPredefinedProcessing() {
			return predefinedProcessing;
		}

		public void setPredefinedProcessing(PredefinedProcessing predefinedProcessing) {
			this.predefinedProcessing = predefinedProcessing;
		}
	}

	// --- MultiProcessing ---
	public static class MultiProcessing {
		private boolean value;
		private MultiProcessingData data;

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public MultiProcessingData getData() {
			return data;
		}

		public void setData(MultiProcessingData data) {
			this.data = data;
		}
	}

	public static class MultiProcessingData {
		private LabelValue multipleProcessingType;
		private LabelValue bunchProcessingType;
		private boolean ungroupBunchedApplicationPrevious;

		public LabelValue getMultipleProcessingType() {
			return multipleProcessingType;
		}

		public void setMultipleProcessingType(LabelValue multipleProcessingType) {
			this.multipleProcessingType = multipleProcessingType;
		}

		public LabelValue getBunchProcessingType() {
			return bunchProcessingType;
		}

		public void setBunchProcessingType(LabelValue bunchProcessingType) {
			this.bunchProcessingType = bunchProcessingType;
		}

		public boolean isUngroupBunchedApplicationPrevious() {
			return ungroupBunchedApplicationPrevious;
		}

		public void setUngroupBunchedApplicationPrevious(boolean ungroupBunchedApplicationPrevious) {
			this.ungroupBunchedApplicationPrevious = ungroupBunchedApplicationPrevious;
		}
	}

	// --- ApplicantEditPermission ---
	public static class ApplicantEditPermission {
		private boolean value;
		private LabelValue control;
		private List<LabelValue> specific;
		
		public LabelValue getControl() {
			return control;
		}

		public void setControl(LabelValue control) {
			this.control = control;
		}

		public List<LabelValue> getSpecific() {
			return specific;
		}

		public void setSpecific(List<LabelValue> specific) {
			this.specific = specific;
		}

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}
	}

	// --- PredefinedProcessing ---
	public static class PredefinedProcessing {
		private List<LabelValue> predefinedProcessingTypes;
		private boolean singleProcessingAllowed;
		private LabelValue predefinedInterval;

		public List<LabelValue> getPredefinedProcessingTypes() {
			return predefinedProcessingTypes;
		}

		public void setPredefinedProcessingTypes(List<LabelValue> predefinedProcessingTypes) {
			this.predefinedProcessingTypes = predefinedProcessingTypes;
		}

		public boolean isSingleProcessingAllowed() {
			return singleProcessingAllowed;
		}

		public void setSingleProcessingAllowed(boolean singleProcessingAllowed) {
			this.singleProcessingAllowed = singleProcessingAllowed;
		}

		public LabelValue getPredefinedInterval() {
			return predefinedInterval;
		}

		public void setPredefinedInterval(LabelValue predefinedInterval) {
			this.predefinedInterval = predefinedInterval;
		}
	}

	// --- DisplayUserTask ---
	public static class DisplayUserTask {
		private boolean value;
		private DisplayUserTaskData data;

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public DisplayUserTaskData getData() {
			return data;
		}

		public void setData(DisplayUserTaskData data) {
			this.data = data;
		}
	}

	public static class DisplayUserTaskData {
		private boolean taskVisibilityOnPreviousTask;
		private boolean userVisibilityOnPreviousTask;

		public boolean isTaskVisibilityOnPreviousTask() {
			return taskVisibilityOnPreviousTask;
		}

		public void setTaskVisibilityOnPreviousTask(boolean taskVisibilityOnPreviousTask) {
			this.taskVisibilityOnPreviousTask = taskVisibilityOnPreviousTask;
		}

		public boolean isUserVisibilityOnPreviousTask() {
			return userVisibilityOnPreviousTask;
		}

		public void setUserVisibilityOnPreviousTask(boolean userVisibilityOnPreviousTask) {
			this.userVisibilityOnPreviousTask = userVisibilityOnPreviousTask;
		}
	}

	// --- ServiceLevel ---
	public static class ServiceLevel {
		private boolean value;
		private ServiceLevelData data;
		private String days; // keep for backward compatibility

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public ServiceLevelData getData() {
			return data;
		}

		public void setData(ServiceLevelData data) {
			this.data = data;
		}

		public String getDays() {
			return days;
		}

		public void setDays(String days) {
			this.days = days;
		}
	}

	public static class ServiceLevelData {
		private String taskTimePeriod;
		private String escalationTimePeriod;

		public String getTaskTimePeriod() {
			return taskTimePeriod;
		}

		public void setTaskTimePeriod(String taskTimePeriod) {
			this.taskTimePeriod = taskTimePeriod;
		}

		public String getEscalationTimePeriod() {
			return escalationTimePeriod;
		}

		public void setEscalationTimePeriod(String escalationTimePeriod) {
			this.escalationTimePeriod = escalationTimePeriod;
		}
	}

	// ----------------- Webservice Details -----------------
	public static class WebserviceDetails {

	    private WsCall wsCall;

	    List<Entities> entities;

	    Definition definition;

	    private LabelValue wsSelected;

	    Boolean unsubscribe;

	    List<LabelValue> applicationStatus;
	    	    
	    public WsCall getWsCall() {
			return wsCall;
		}
		public void setWsCall(WsCall wsCall) {
			this.wsCall = wsCall;
		}
		
		public Definition getDefinition() {
			return definition;
		}
		public void setDefinition(Definition definition) {
			this.definition = definition;
		}
		public LabelValue getWsSelected() {
			return wsSelected;
		}
		public void setWsSelected(LabelValue wsSelected) {
			this.wsSelected = wsSelected;
		}
		
		public List<Entities> getEntities() {
			return entities;
		}
		public void setEntities(List<Entities> entities) {
			this.entities = entities;
		}
		public Boolean getUnsubscribe() {
			return unsubscribe;
		}
		public void setUnsubscribe(Boolean unsubscribe) {
			this.unsubscribe = unsubscribe;
		}
		public List<LabelValue> getApplicationStatus() {
			return applicationStatus;
		}
		public void setApplicationStatus(List<LabelValue> applicationStatus) {
			this.applicationStatus = applicationStatus;
		}

		public static class WsCall {

	        private String wsCallInterval;

	        private String wsCallFrequency;

	        private LabelValue wsCallIntervalUnit;

			public String getWsCallInterval() {
				return wsCallInterval;
			}

			public void setWsCallInterval(String wsCallInterval) {
				this.wsCallInterval = wsCallInterval;
			}

			public String getWsCallFrequency() {
				return wsCallFrequency;
			}

			public void setWsCallFrequency(String wsCallFrequency) {
				this.wsCallFrequency = wsCallFrequency;
			}

			public LabelValue getWsCallIntervalUnit() {
				return wsCallIntervalUnit;
			}

			public void setWsCallIntervalUnit(LabelValue wsCallIntervalUnit) {
				this.wsCallIntervalUnit = wsCallIntervalUnit;
			}
	        
	    }
	    public static class Definition {

	        private String id;

	        private String templateName;

	        private String description;

	        private String endpoint;

	        private String method;

	        private String callType;

	        private Boolean storeResponse;

	        private Boolean serverValidation;

	        private Body body;

	        private List<Header> headers;

	        private List<Param> params;

	        private Authorization authorization;

	        private List<DataMapping> dataMapping;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getTemplateName() {
				return templateName;
			}

			public void setTemplateName(String templateName) {
				this.templateName = templateName;
			}

			public String getDescription() {
				return description;
			}

			public void setDescription(String description) {
				this.description = description;
			}

			public String getEndpoint() {
				return endpoint;
			}

			public void setEndpoint(String endpoint) {
				this.endpoint = endpoint;
			}

			public String getMethod() {
				return method;
			}

			public void setMethod(String method) {
				this.method = method;
			}

			public String getCallType() {
				return callType;
			}

			public void setCallType(String callType) {
				this.callType = callType;
			}

			public Boolean getStoreResponse() {
				return storeResponse;
			}

			public void setStoreResponse(Boolean storeResponse) {
				this.storeResponse = storeResponse;
			}

			public Boolean getServerValidation() {
				return serverValidation;
			}

			public void setServerValidation(Boolean serverValidation) {
				this.serverValidation = serverValidation;
			}

			public Body getBody() {
				return body;
			}

			public void setBody(Body body) {
				this.body = body;
			}

			public List<Header> getHeaders() {
				return headers;
			}

			public void setHeaders(List<Header> headers) {
				this.headers = headers;
			}

			public List<Param> getParams() {
				return params;
			}

			public void setParams(List<Param> params) {
				this.params = params;
			}

			public Authorization getAuthorization() {
				return authorization;
			}

			public void setAuthorization(Authorization authorization) {
				this.authorization = authorization;
			}

			public List<DataMapping> getDataMapping() {
				return dataMapping;
			}

			public void setDataMapping(List<DataMapping> dataMapping) {
				this.dataMapping = dataMapping;
			}
	        
	    }
	    
	    public static class Header {
	        private String label;
	        private String value;
	        private String fieldValue;
			public String getLabel() {
				return label;
			}
			public void setLabel(String label) {
				this.label = label;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public String getFieldValue() {
				return fieldValue;
			}
			public void setFieldValue(String fieldValue) {
				this.fieldValue = fieldValue;
			}
	    }
	    public static class Param {
	        private String label;
	        private String value;
	        private String fieldValue;
			public String getLabel() {
				return label;
			}
			public void setLabel(String label) {
				this.label = label;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public String getFieldValue() {
				return fieldValue;
			}
			public void setFieldValue(String fieldValue) {
				this.fieldValue = fieldValue;
			}
	    }
	    
	    public static class Body {

	        private String type;
	        private String raw;
	        private String rawFormat;

	        private List<FormData> formData;
	        private List<UrlEncoded> urlEncoded;
	        private List<FormData> rawJson;
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getRaw() {
				return raw;
			}
			public void setRaw(String raw) {
				this.raw = raw;
			}
			public String getRawFormat() {
				return rawFormat;
			}
			public void setRawFormat(String rawFormat) {
				this.rawFormat = rawFormat;
			}
			public List<FormData> getFormData() {
				return formData;
			}
			public void setFormData(List<FormData> formData) {
				this.formData = formData;
			}
			public List<UrlEncoded> getUrlEncoded() {
				return urlEncoded;
			}
			public void setUrlEncoded(List<UrlEncoded> urlEncoded) {
				this.urlEncoded = urlEncoded;
			}
			public List<FormData> getRawJson() {
				return rawJson;
			}
			public void setRawJson(List<FormData> rawJson) {
				this.rawJson = rawJson;
			}
	    }

	    public static class FormData {
	        private String label;
	        private String value;
	        private String fieldValue;
			public String getLabel() {
				return label;
			}
			public void setLabel(String label) {
				this.label = label;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public String getFieldValue() {
				return fieldValue;
			}
			public void setFieldValue(String fieldValue) {
				this.fieldValue = fieldValue;
			}
	    }

	    public static class UrlEncoded {
	        private String label;
	        private String value;
	        private String fieldValue;
			public String getLabel() {
				return label;
			}
			public void setLabel(String label) {
				this.label = label;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public String getFieldValue() {
				return fieldValue;
			}
			public void setFieldValue(String fieldValue) {
				this.fieldValue = fieldValue;
			}
	    }
	    
	    public static class Authorization {

	        private String type;

	        private ApiKey apiKey;
	        private Bearer bearer;
	        private Basic basic;
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public ApiKey getApiKey() {
				return apiKey;
			}
			public void setApiKey(ApiKey apiKey) {
				this.apiKey = apiKey;
			}
			public Bearer getBearer() {
				return bearer;
			}
			public void setBearer(Bearer bearer) {
				this.bearer = bearer;
			}
			public Basic getBasic() {
				return basic;
			}
			public void setBasic(Basic basic) {
				this.basic = basic;
			}
	        
	    }

	    
	    public static class ApiKey {
	        private String key;
	        private String value;
	        private String addTo;
			public String getKey() {
				return key;
			}
			public void setKey(String key) {
				this.key = key;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public String getAddTo() {
				return addTo;
			}
			public void setAddTo(String addTo) {
				this.addTo = addTo;
			}
	        
	    }

	    
	    public static class Bearer {
	        private String token;
			public String getToken() {
				return token;
			}
			public void setToken(String token) {
				this.token = token;
			}
	    }
	    public static class Basic {
	        private String username;
	        private String password;
			public String getUsername() {
				return username;
			}
			public void setUsername(String username) {
				this.username = username;
			}
			public String getPassword() {
				return password;
			}
			public void setPassword(String password) {
				this.password = password;
			}
	        
	    }
	    public static class DataMapping {

	        private String targetType;
	        private String label;
	        private String dataType;
	        private String explicitKeys;
	        private String valueFormat;
	        private String labelName;
	        private String mapWith;
			public String getTargetType() {
				return targetType;
			}
			public void setTargetType(String targetType) {
				this.targetType = targetType;
			}
			public String getLabel() {
				return label;
			}
			public void setLabel(String label) {
				this.label = label;
			}
			public String getDataType() {
				return dataType;
			}
			public void setDataType(String dataType) {
				this.dataType = dataType;
			}
			public String getExplicitKeys() {
				return explicitKeys;
			}
			public void setExplicitKeys(String explicitKeys) {
				this.explicitKeys = explicitKeys;
			}
			public String getValueFormat() {
				return valueFormat;
			}
			public void setValueFormat(String valueFormat) {
				this.valueFormat = valueFormat;
			}
			public String getLabelName() {
				return labelName;
			}
			public void setLabelName(String labelName) {
				this.labelName = labelName;
			}
			public String getMapWith() {
				return mapWith;
			}
			public void setMapWith(String mapWith) {
				this.mapWith = mapWith;
			}
	        
	        
	    }
	   
	}
	
	public static class ExternalSystemDetail {

		private Long externalSystemRegId;
		private Boolean isDataFromExternalSys;
		private Boolean captureAadhaar;
		private String gazettedNotificationFile;
		private List<MappingData> mappingData;

		public Long getExternalSystemRegId() {
			return externalSystemRegId;
		}

		public void setExternalSystemRegId(Long externalSystemRegId) {
			this.externalSystemRegId = externalSystemRegId;
		}

		public Boolean getIsDataFromExternalSys() {
			return isDataFromExternalSys;
		}

		public void setIsDataFromExternalSys(Boolean isDataFromExternalSys) {
			this.isDataFromExternalSys = isDataFromExternalSys;
		}

		public Boolean getCaptureAadhaar() {
			return captureAadhaar;
		}

		public void setCaptureAadhaar(Boolean captureAadhaar) {
			this.captureAadhaar = captureAadhaar;
		}

		public String getGazettedNotificationFile() {
			return gazettedNotificationFile;
		}

		public void setGazettedNotificationFile(String gazettedNotificationFile) {
			this.gazettedNotificationFile = gazettedNotificationFile;
		}

		public List<MappingData> getMappingData() {
			return mappingData;
		}

		public void setMappingData(List<MappingData> mappingData) {
			this.mappingData = mappingData;
		}
	}

	public static class MappingData {

		private String nodeName;
		private String nodeReference;
		private String description;
		private String uniqueId;
		private Boolean editable;

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

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUniqueId() {
			return uniqueId;
		}

		public void setUniqueId(String uniqueId) {
			this.uniqueId = uniqueId;
		}

		public Boolean getEditable() {
			return editable;
		}

		public void setEditable(Boolean editable) {
			this.editable = editable;
		}
	}

	public static class AutoDelete {

		private Map<String, String> data; // dynamic keys like "mode_111": "12"
		private boolean behaviour;

		public Map<String, String> getData() {
			return data;
		}

		public void setData(Map<String, String> data) {
			this.data = data;
		}

		public boolean isBehaviour() {
			return behaviour;
		}

		public void setBehaviour(boolean behaviour) {
			this.behaviour = behaviour;
		}
	}

	public static class LimitSubmission {

		private Map<String, LimitData> data;
		private boolean behaviour;

		public Map<String, LimitData> getData() {
			return data;
		}

		public void setData(Map<String, LimitData> data) {
			this.data = data;
		}

		public boolean isBehaviour() {
			return behaviour;
		}

		public void setBehaviour(boolean behaviour) {
			this.behaviour = behaviour;
		}

		public static class LimitData {
			private String yearly;
			private String monthly;

			public String getYearly() {
				return yearly;
			}

			public void setYearly(String yearly) {
				this.yearly = yearly;
			}

			public String getMonthly() {
				return monthly;
			}

			public void setMonthly(String monthly) {
				this.monthly = monthly;
			}
		}
	}

	// ----------------- Edge DTO -----------------
	public static class EdgeDTO {
		private String source;
		private String target;
		private String sourceHandle;
		private String targetHandle;
		private String type;
		private MarkerEnd markerEnd;
		private String id;

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getSourceHandle() {
			return sourceHandle;
		}

		public void setSourceHandle(String sourceHandle) {
			this.sourceHandle = sourceHandle;
		}

		public String getTargetHandle() {
			return targetHandle;
		}

		public void setTargetHandle(String targetHandle) {
			this.targetHandle = targetHandle;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public MarkerEnd getMarkerEnd() {
			return markerEnd;
		}

		public void setMarkerEnd(MarkerEnd markerEnd) {
			this.markerEnd = markerEnd;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public static class MarkerEnd {
			private String type;
			private int width;
			private int height;
			private String color;

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public int getWidth() {
				return width;
			}

			public void setWidth(int width) {
				this.width = width;
			}

			public int getHeight() {
				return height;
			}

			public void setHeight(int height) {
				this.height = height;
			}

			public String getColor() {
				return color;
			}

			public void setColor(String color) {
				this.color = color;
			}
		}
	}
	public static class AssociatedTaskDTO {

	    private String id;

	    private String name;

	    private String description;

	    private LabelValue associatedTaskType;

	    private Boolean enabled;

	    // One associated task can be attached to multiple transitions
	    //private List<AssociatedTaskLinkDTO> links;

	    // Official Intimation
	    private OfficialIntimation officialIntimation;

	    // Existing DTO
	    @JsonAlias("webServiceDetails")
	    private WebserviceDetails webserviceDetails;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public LabelValue getAssociatedTaskType() {
			return associatedTaskType;
		}

		public void setAssociatedTaskType(LabelValue associatedTaskType) {
			this.associatedTaskType = associatedTaskType;
		}

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}

//		public List<AssociatedTaskLinkDTO> getLinks() {
//			return links;
//		}
//
//		public void setLinks(List<AssociatedTaskLinkDTO> links) {
//			this.links = links;
//		}

		public OfficialIntimation getOfficialIntimation() {
			return officialIntimation;
		}

		public WebserviceDetails getWebserviceDetails() {
			return webserviceDetails;
		}

		public void setWebserviceDetails(WebserviceDetails webserviceDetails) {
			this.webserviceDetails = webserviceDetails;
		}

		public void setOfficialIntimation(OfficialIntimation officialIntimation) {
			this.officialIntimation = officialIntimation;
		}
	}
	public static class OfficialIntimation {

	    private List<Entities> entities;

	    private Boolean allowApplicationView;

	    private Boolean allowHistoryView;

	    private Boolean autoClear;

		public List<Entities> getEntities() {
			return entities;
		}

		public void setEntities(List<Entities> entities) {
			this.entities = entities;
		}

		public Boolean getAllowApplicationView() {
			return allowApplicationView;
		}

		public void setAllowApplicationView(Boolean allowApplicationView) {
			this.allowApplicationView = allowApplicationView;
		}

		public Boolean getAllowHistoryView() {
			return allowHistoryView;
		}

		public void setAllowHistoryView(Boolean allowHistoryView) {
			this.allowHistoryView = allowHistoryView;
		}

		public Boolean getAutoClear() {
			return autoClear;
		}

		public void setAutoClear(Boolean autoClear) {
			this.autoClear = autoClear;
		}
	}
	public static class AssociatedTaskLinkDTO {

	    private String edgeId;
	    
	    private String sourceNodeId;
	    
	    private String targetNodeId;
	    
	    private LabelValue triggerPoint;

		public String getEdgeId() {
			return edgeId;
		}

		public void setEdgeId(String edgeId) {
			this.edgeId = edgeId;
		}

		public String getSourceNodeId() {
			return sourceNodeId;
		}

		public void setSourceNodeId(String sourceNodeId) {
			this.sourceNodeId = sourceNodeId;
		}

		public String getTargetNodeId() {
			return targetNodeId;
		}

		public void setTargetNodeId(String targetNodeId) {
			this.targetNodeId = targetNodeId;
		}

		public LabelValue getTriggerPoint() {
			return triggerPoint;
		}

		public void setTriggerPoint(LabelValue triggerPoint) {
			this.triggerPoint = triggerPoint;
		}	    
	}
}