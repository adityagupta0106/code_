package com.serviceplus.metadata.notification.dto;

import java.io.Serializable;
import java.util.Map;

import com.serviceplus.metadata.notification.dto.NotificationDTO.EmailConfig;
import com.serviceplus.metadata.notification.dto.NotificationDTO.SandesConfig;
import com.serviceplus.metadata.notification.dto.NotificationDTO.SmsConfig;
import com.serviceplus.metadata.notification.dto.NotificationDTO.WhatsappConfig;
import com.serviceplus.metadata.notification.enums.NotificationType;

public class NotificationConfigEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	private String eventType;
	private Integer serviceId;
	private Map<Long, NotificationTempleConfig> notificationTempleConfigMap;
	private Map<Long, NotificationProviderConfig> notificationProviderConfigMap;

	public static class NotificationProviderConfig {
		private Long id;
		private NotificationType type;
		private ConfigCommonDTO configDTO;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public NotificationType getType() {
			return type;
		}

		public void setType(NotificationType type) {
			this.type = type;
		}

		public ConfigCommonDTO getConfigDTO() {
			return configDTO;
		}

		public void setConfigDTO(ConfigCommonDTO configDTO) {
			this.configDTO = configDTO;
		}

	}

	public static class NotificationTempleConfig {
		private Long notificationId;
		private String taskId;
		private NotificationType channel;
		
		private EmailConfig emailConfig;
		private SmsConfig smsConfig;
		private WhatsappConfig whatsappConfig;
		private SandesConfig sandesConfig;

		public Long getNotificationId() {
			return notificationId;
		}

		public void setNotificationId(Long notificationId) {
			this.notificationId = notificationId;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
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

	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Map<Long, NotificationTempleConfig> getNotificationTempleConfigMap() {
		return notificationTempleConfigMap;
	}

	public void setNotificationTempleConfigMap(Map<Long, NotificationTempleConfig> notificationTempleConfigMap) {
		this.notificationTempleConfigMap = notificationTempleConfigMap;
	}

	public Map<Long, NotificationProviderConfig> getNotificationProviderConfigMap() {
		return notificationProviderConfigMap;
	}

	public void setNotificationProviderConfigMap(Map<Long, NotificationProviderConfig> notificationProviderConfigMap) {
		this.notificationProviderConfigMap = notificationProviderConfigMap;
	}

}
