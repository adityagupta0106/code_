package com.serviceplus.metadata.notification.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.document.mapping.dto.TaskDocs;
import com.serviceplus.metadata.notification.enums.NotificationType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {

	private Integer serviceId;
	private String serviceName;
	private String taskId;
	private String taskName;
	private List<Object> allowTaskAction;
	private String notificationName;
	private String triggerPointId;
	private String triggerPointName;
	private List<Integer> serviceVersions;

	private NotificationType channel;

	private EmailConfig emailConfig;
	private SmsConfig smsConfig;
	private WhatsappConfig whatsappConfig;
	private SandesConfig sandesConfig;

	private Boolean auditEnabled;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class EmailConfig {

		private String fromFieldId;
		private List<RecipientField> recipientFieldId;
		private List<RecipientField> ccFieldId;
		private List<TaskDocs> attachmentDocumentIds;
		private String subject;
		private String body;
		private Set<String> usedAttr;

		public String getFromFieldId() {
			return fromFieldId;
		}

		public void setFromFieldId(String fromFieldId) {
			this.fromFieldId = fromFieldId;
		}

		public List<RecipientField> getRecipientFieldId() {
			return recipientFieldId;
		}

		public void setRecipientFieldId(List<RecipientField> recipientFieldId) {
			this.recipientFieldId = recipientFieldId;
		}

		public List<RecipientField> getCcFieldId() {
			return ccFieldId;
		}

		public void setCcFieldId(List<RecipientField> ccFieldId) {
			this.ccFieldId = ccFieldId;
		}

		public List<TaskDocs> getAttachmentDocumentIds() {
			return attachmentDocumentIds;
		}

		public void setAttachmentDocumentIds(List<TaskDocs> attachmentDocumentIds) {
			this.attachmentDocumentIds = attachmentDocumentIds;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public Set<String> getUsedAttr() {
			return usedAttr;
		}

		public void setUsedAttr(Set<String> usedAttr) {
			this.usedAttr = usedAttr;
		}


	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SmsConfig {

		private Long smsConfigId;
		private String dlt;
		private List<RecipientField> targetFieldId;
		private Set<String> usedAttr;
		private String message;

		public Long getSmsConfigId() {
			return smsConfigId;
		}

		public void setSmsConfigId(Long smsConfigId) {
			this.smsConfigId = smsConfigId;
		}

		public String getDlt() {
			return dlt;
		}

		public void setDlt(String dlt) {
			this.dlt = dlt;
		}

		public List<RecipientField> getTargetFieldId() {
			return targetFieldId;
		}

		public void setTargetFieldId(List<RecipientField> targetFieldId) {
			this.targetFieldId = targetFieldId;
		}

		public Set<String> getUsedAttr() {
			return usedAttr;
		}

		public void setUsedAttr(Set<String> usedAttr) {
			this.usedAttr = usedAttr;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SandesConfig {

		private Long sandesConfigId;
		private List<TaskDocs> attachmentDocumentIds;
		private List<RecipientField> targetFieldId;

		private Set<String> usedAttr;
		private String message;

		public Long getSandesConfigId() {
			return sandesConfigId;
		}

		public void setSandesConfigId(Long sandesConfigId) {
			this.sandesConfigId = sandesConfigId;
		}

		public List<TaskDocs> getAttachmentDocumentIds() {
			return attachmentDocumentIds;
		}

		public void setAttachmentDocumentIds(List<TaskDocs> attachmentDocumentIds) {
			this.attachmentDocumentIds = attachmentDocumentIds;
		}

		public List<RecipientField> getTargetFieldId() {
			return targetFieldId;
		}

		public void setTargetFieldId(List<RecipientField> targetFieldId) {
			this.targetFieldId = targetFieldId;
		}

		public Set<String> getUsedAttr() {
			return usedAttr;
		}

		public void setUsedAttr(Set<String> usedAttr) {
			this.usedAttr = usedAttr;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class WhatsappConfig {

		private Long whatsappConfigId;
		private String header;
		private String targetFieldId;
		private String footer;
		private Set<String> usedAttr;
		private String message;

		public Long getWhatsappConfigId() {
			return whatsappConfigId;
		}

		public void setWhatsappConfigId(Long whatsappConfigId) {
			this.whatsappConfigId = whatsappConfigId;
		}

		public String getHeader() {
			return header;
		}

		public void setHeader(String header) {
			this.header = header;
		}

		public String getTargetFieldId() {
			return targetFieldId;
		}

		public void setTargetFieldId(String targetFieldId) {
			this.targetFieldId = targetFieldId;
		}

		public String getFooter() {
			return footer;
		}

		public void setFooter(String footer) {
			this.footer = footer;
		}

		public Set<String> getUsedAttr() {
			return usedAttr;
		}

		public void setUsedAttr(Set<String> usedAttr) {
			this.usedAttr = usedAttr;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RecipientField {

	    private String fieldType;
	    private String toValue;

	    public String getFieldType() {
	        return fieldType;
	    }

	    public void setFieldType(String fieldType) {
	        this.fieldType = fieldType;
	    }

	    public String getToValue() {
	        return toValue;
	    }

	    public void setToValue(String toValue) {
	        this.toValue = toValue;
	    }
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

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	public String getTriggerPointId() {
		return triggerPointId;
	}

	public void setTriggerPointId(String triggerPointId) {
		this.triggerPointId = triggerPointId;
	}

	public String getTriggerPointName() {
		return triggerPointName;
	}

	public void setTriggerPointName(String triggerPointName) {
		this.triggerPointName = triggerPointName;
	}

	public List<Integer> getServiceVersions() {
		return serviceVersions;
	}

	public void setServiceVersions(List<Integer> serviceVersions) {
		this.serviceVersions = serviceVersions;
	}

	public NotificationType getChannel() {
		return channel;
	}

	public void setChannel(NotificationType channel) {
		this.channel = channel;
	}

	public EmailConfig getEmailConfig() {
		return emailConfig;
	}

	public void setEmailConfig(EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
	}

	public SmsConfig getSmsConfig() {
		return smsConfig;
	}

	public void setSmsConfig(SmsConfig smsConfig) {
		this.smsConfig = smsConfig;
	}

	public WhatsappConfig getWhatsappConfig() {
		return whatsappConfig;
	}

	public void setWhatsappConfig(WhatsappConfig whatsappConfig) {
		this.whatsappConfig = whatsappConfig;
	}

	public SandesConfig getSandesConfig() {
		return sandesConfig;
	}

	public void setSandesConfig(SandesConfig sandesConfig) {
		this.sandesConfig = sandesConfig;
	}

	public Boolean getAuditEnabled() {
		return auditEnabled;
	}

	public void setAuditEnabled(Boolean auditEnabled) {
		this.auditEnabled = auditEnabled;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public List<Object> getAllowTaskAction() {
		return allowTaskAction;
	}

	public void setAllowTaskAction(List<Object> allowTaskAction) {
		this.allowTaskAction = allowTaskAction;
	}

}
