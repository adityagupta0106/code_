package com.serviceplus.tracking.dto;

import java.util.List;

public class SentBoxSearchRequest {

    private Integer page = 0;

    private Integer size = 50;

    private Integer baseServiceId;

    private String taskId;

    private String applRefNo;

    private Integer actionCode;

    private Long serviceId;
    private List<InboxApplReqDTO.FilterDTO> filters;
    
    public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public List<InboxApplReqDTO.FilterDTO> getFilters() {
		return filters;
	}

	public void setFilters(List<InboxApplReqDTO.FilterDTO> filters) {
		this.filters = filters;
	}

	public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getApplRefNo() {
        return applRefNo;
    }

    public void setApplRefNo(String applRefNo) {
        this.applRefNo = applRefNo;
    }

    public Integer getActionCode() {
        return actionCode;
    }

    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
    }
}
