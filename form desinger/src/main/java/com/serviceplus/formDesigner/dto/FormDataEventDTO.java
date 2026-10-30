package com.serviceplus.formDesigner.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.serviceplus.formDesigner.request.model.ApplicationData;

public class FormDataEventDTO {

    private String applicationId;
    private Integer serviceId;
    private List<String> taskIds;
    private String holderId;
    private String tenantId;
    private Map<String, Object> formData;
    private Set<String> keys;
    private UsedAttributeListDTO usedAttributeList;

    public FormDataEventDTO(ApplicationData formData, List<String> taskIds) {
		super();
		this.applicationId = formData.getApplId();
		this.serviceId = formData.getServiceId();
		this.taskIds = taskIds;
		this.holderId = formData.getHolderId();
		this.tenantId = formData.getTenantId();
		this.formData = (Map<String, Object>) formData.getFormData();
	}
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

	public List<String> getTaskIds() {
		return taskIds;
	}
	public void setTaskIds(List<String> taskIds) {
		this.taskIds = taskIds;
	}
	public Map<String, Object> getFormData() {
		return formData;
	}

	public void setFormData(Map<String, Object> formData) {
		this.formData = formData;
	}

	public Set<String> getKeys() {
		return keys;
	}

	public void setKeys(Set<String> keys) {
		this.keys = keys;
	}
	public String getHolderId() {
		return holderId;
	}
	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

    public UsedAttributeListDTO getUsedAttributeList() {
        return usedAttributeList;
    }

    public void setUsedAttributeList(UsedAttributeListDTO usedAttributeList) {
        this.usedAttributeList = usedAttributeList;
    }
}
