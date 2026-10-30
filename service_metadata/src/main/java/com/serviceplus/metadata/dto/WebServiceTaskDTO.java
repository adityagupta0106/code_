package com.serviceplus.metadata.dto;

public class WebServiceTaskDTO {

    private String taskId;

    private String taskName;

    private ProcessFlowDTO.WebserviceDetails webserviceDetails;

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

    public ProcessFlowDTO.WebserviceDetails getWebserviceDetails() {
        return webserviceDetails;
    }

    public void setWebserviceDetails(ProcessFlowDTO.WebserviceDetails webserviceDetails) {
        this.webserviceDetails = webserviceDetails;
    }
}
