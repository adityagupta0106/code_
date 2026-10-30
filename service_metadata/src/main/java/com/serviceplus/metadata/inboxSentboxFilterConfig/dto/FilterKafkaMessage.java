package com.serviceplus.metadata.inboxSentboxFilterConfig.dto;

import java.util.List;

public class FilterKafkaMessage {

	private List<FilterKafkaDTO> configurations;

	public static class FilterKafkaDTO {

		private Long filterId;
		private Long baseServiceId;
		private Long serviceId;
		private String taskId;
		private Character filterType;
		private String tenantId;

		private Character attrType;
		private String attrFormId;
		private String attrId;
		private String attrLabel;
		private Integer attrOrder;
		private String filterCondition;

		public Long getFilterId() {
			return filterId;
		}

		public void setFilterId(Long filterId) {
			this.filterId = filterId;
		}

		public Long getBaseServiceId() {
			return baseServiceId;
		}

		public void setBaseServiceId(Long baseServiceId) {
			this.baseServiceId = baseServiceId;
		}

		public Character getFilterType() {
			return filterType;
		}

		public void setFilterType(Character filterType) {
			this.filterType = filterType;
		}

		public Long getServiceId() {
			return serviceId;
		}

		public void setServiceId(Long serviceId) {
			this.serviceId = serviceId;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}

		public Character getAttrType() {
			return attrType;
		}

		public void setAttrType(Character attrType) {
			this.attrType = attrType;
		}

		public String getAttrFormId() {
			return attrFormId;
		}

		public void setAttrFormId(String attrFormId) {
			this.attrFormId = attrFormId;
		}

		public String getAttrId() {
			return attrId;
		}

		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}

		public Integer getAttrOrder() {
			return attrOrder;
		}

		public void setAttrOrder(Integer attrOrder) {
			this.attrOrder = attrOrder;
		}

		public String getAttrLabel() {
			return attrLabel;
		}

		public void setAttrLabel(String attrLabel) {
			this.attrLabel = attrLabel;
		}

		public String getFilterCondition() {
			return filterCondition;
		}

		public void setFilterCondition(String filterCondition) {
			this.filterCondition = filterCondition;
		}

	}

	public List<FilterKafkaDTO> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<FilterKafkaDTO> configurations) {
		this.configurations = configurations;
	}
}