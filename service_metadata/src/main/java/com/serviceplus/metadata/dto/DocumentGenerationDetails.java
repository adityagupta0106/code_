package com.serviceplus.metadata.dto;

import com.serviceplus.metadata.document.mapping.dto.DocMappingDTO;

import java.util.List;

public class DocumentGenerationDetails {

    private String taskId;

    private List<DocMappingDTO> documentMapping;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<DocMappingDTO> getDocumentMapping() {
        return documentMapping;
    }

    public void setDocumentMapping(List<DocMappingDTO> documentMapping) {
        this.documentMapping = documentMapping;
    }
}
