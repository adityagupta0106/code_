package com.serviceplus.metadata.dto;

import java.util.List;

public class CommitResponse {

    private List<CommitResult> results;

    public static class CommitResult {

        private String uploadId;
        private String status;
        private String message;

        public CommitResult(String uploadId, String status, String message) {
            this.uploadId = uploadId;
            this.status = status;
            this.message = message;
        }

        public String getUploadId() { return uploadId; }

        public void setUploadId(String uploadId) {
            this.uploadId = uploadId;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }

    public CommitResponse(List<CommitResult> results) {
        this.results = results;
    }

    public List<CommitResult> getResults() {
        return results;
    }

    public void setResults(List<CommitResult> results) {
        this.results = results;
    }
}
