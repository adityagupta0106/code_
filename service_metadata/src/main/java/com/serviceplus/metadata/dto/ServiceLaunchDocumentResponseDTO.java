package com.serviceplus.metadata.dto;

public class ServiceLaunchDocumentResponseDTO {

    private Integer serviceId;

    private String fileId;

    private String base64;

    private String documentType;

    private String message;

    public ServiceLaunchDocumentResponseDTO() {
    }

    public ServiceLaunchDocumentResponseDTO(
            Integer serviceId,
            String fileId,
            String base64,
            String documentType,
            String message) {

        this.serviceId = serviceId;
        this.fileId = fileId;
        this.base64 = base64;
        this.documentType = documentType;
        this.message = message;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
