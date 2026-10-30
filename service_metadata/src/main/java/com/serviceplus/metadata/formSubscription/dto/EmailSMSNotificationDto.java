package com.serviceplus.metadata.formSubscription.dto;

import java.io.Serializable;

import org.springframework.lang.NonNull;

public class EmailSMSNotificationDto implements Serializable{

	private static final long serialVersionUID = 1L;
	private EmailDetails email;
	private SMSDetails sms;
	@NonNull
	private Integer serviceId;
	@NonNull
	private Integer applicationId;
	
	public static class EmailDetails implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@NonNull
		private String fromEmail;
		@NonNull
		private String beneficiary;
		@NonNull
		private String content;
		private String copyToAccounts;
		private String attachement;
		private String attachementType;
		@NonNull
		private String subject;
		
		public String getFromEmail() {
			return fromEmail;
		}
		public void setFromEmail(String fromEmail) {
			this.fromEmail = fromEmail;
		}
		public String getToEmail() {
			return beneficiary;
		}
		public void setToEmail(String toEmail) {
			this.beneficiary = toEmail;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getAttachement() {
			return attachement;
		}
		public void setAttachement(String attachement) {
			this.attachement = attachement;
		}
		public String getAttachementType() {
			return attachementType;
		}
		public void setAttachementType(String attachementType) {
			this.attachementType = attachementType;
		}
		public String getSubject() {
			return subject;
		}
		public void setSubject(String subject) {
			this.subject = subject;
		}
		public String getBeneficiary() {
			return beneficiary;
		}
		public void setBeneficiary(String beneficiary) {
			this.beneficiary = beneficiary;
		}
		public String getCopyToAccounts() {
			return copyToAccounts;
		}
		public void setCopyToAccounts(String copyToAccounts) {
			this.copyToAccounts = copyToAccounts;
		}
	}
	
	public static class SMSDetails implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@NonNull
		private Integer beneficiary; 
		@NonNull
		private Integer countryCode;
		@NonNull
		private String content;
		private String smsProvider;
		
		public Integer getBeneficiary() {
			return beneficiary;
		}
		public void setBeneficiary(Integer beneficiary) {
			this.beneficiary = beneficiary;
		}
		public Integer getCountryCode() {
			return countryCode;
		}
		public void setCountryCode(Integer countryCode) {
			this.countryCode = countryCode;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getSmsProvider() {
			return smsProvider;
		}
		public void setSmsProvider(String smsProvider) {
			this.smsProvider = smsProvider;
		}
	}

	public EmailDetails getEmail() {
		return email;
	}

	public void setEmail(EmailDetails email) {
		this.email = email;
	}

	public SMSDetails getSms() {
		return sms;
	}

	public void setSms(SMSDetails sms) {
		this.sms = sms;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}
}