package com.serviceplus.tracking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationTrackingResponse {

    private String applId;

    private String applRefNo;

    private String beneficiaryUserName;

    private String serviceName;

    private LocalDateTime applyDate;

    private List<TrackingDetailDTO> trackingDetails;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TrackingPermissions {

        private Boolean canEdit;
        private Boolean canMakePayment;
        private Boolean canViewRemarks;
        private Boolean canPreviewForm;
        private Boolean canDownloadCertificate;

        public Boolean getCanEdit() {
            return canEdit;
        }

        public void setCanEdit(Boolean canEdit) {
            this.canEdit = canEdit;
        }

        public Boolean getCanMakePayment() {
            return canMakePayment;
        }

        public void setCanMakePayment(Boolean canMakePayment) {
            this.canMakePayment = canMakePayment;
        }

        public Boolean getCanViewRemarks() {
            return canViewRemarks;
        }

        public void setCanViewRemarks(Boolean canViewRemarks) {
            this.canViewRemarks = canViewRemarks;
        }

        public Boolean getCanPreviewForm() {
            return canPreviewForm;
        }

        public void setCanPreviewForm(Boolean canPreviewForm) {
            this.canPreviewForm = canPreviewForm;
        }

        public Boolean getCanDownloadCertificate() {
            return canDownloadCertificate;
        }

        public void setCanDownloadCertificate(Boolean canDownloadCertificate) {
            this.canDownloadCertificate = canDownloadCertificate;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TrackingDetailDTO {

        private String processId;

        private String taskName;

        private String actionStatus;

        private String formId;

        private String dataId;

        private LocalDateTime actionOn;

        private String actionTaken;

        private TrackingPermissions permissions;

        private Integer actionCode;

        private String actionName;

        private List<DocumentDTO> documents;
        
        private String actionRemarks;

        public String getActionRemarks() {
            return actionRemarks;
        }

        public void setActionRemarks(String actionRemarks) {
            this.actionRemarks = actionRemarks;
        }


        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class DocumentDTO {

            private Long documentId;

            private String documentName;

            public Long getDocumentId() {
                return documentId;
            }

            public void setDocumentId(Long documentId) {
                this.documentId = documentId;
            }

            public String getDocumentName() {
                return documentName;
            }

            public void setDocumentName(String documentName) {
                this.documentName = documentName;
            }
        }


        public LocalDateTime getActionOn() {
            return actionOn;
        }

        public void setActionOn(LocalDateTime actionOn) {
            this.actionOn = actionOn;
        }

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        public String getFormId() {
            return formId;
        }

        public void setFormId(String formId) {
            this.formId = formId;
        }

        public String getActionStatus() {
            return actionStatus;
        }

        public void setActionStatus(String actionStatus) {
            this.actionStatus = actionStatus;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getProcessId() {
            return processId;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }

        public String getActionTaken() {
            return actionTaken;
        }

        public void setActionTaken(String actionTaken) {
            this.actionTaken = actionTaken;
        }

        public TrackingPermissions getPermissions() {
            return permissions;
        }

        public void setPermissions(TrackingPermissions permissions) {
            this.permissions = permissions;
        }

        public Integer getActionCode() {
            return actionCode;
        }

        public void setActionCode(Integer actionCode) {
            this.actionCode = actionCode;
        }

        public String getActionName() {
            return actionName;
        }

        public void setActionName(String actionName) {
            this.actionName = actionName;
        }

        public List<DocumentDTO> getDocuments() {
            return documents;
        }

        public void setDocuments(List<DocumentDTO> documents) {
            this.documents = documents;
        }
    }

    public String getApplId() {
        return applId;
    }

    public void setApplId(String applId) {
        this.applId = applId;
    }

    public List<TrackingDetailDTO> getTrackingDetails() {
        return trackingDetails;
    }

    public void setTrackingDetails(List<TrackingDetailDTO> trackingDetails) {
        this.trackingDetails = trackingDetails;
    }

    public LocalDateTime getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(LocalDateTime applyDate) {
        this.applyDate = applyDate;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBeneficiaryUserName() {
        return beneficiaryUserName;
    }

    public void setBeneficiaryUserName(String beneficiaryUserName) {
        this.beneficiaryUserName = beneficiaryUserName;
    }

    public String getApplRefNo() {
        return applRefNo;
    }

    public void setApplRefNo(String applRefNo) {
        this.applRefNo = applRefNo;
    }

}
