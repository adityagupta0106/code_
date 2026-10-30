package com.serviceplus.tracking.dto;

public class DocumentTinyUrlRequestDTO {

    private String urlKey;

    private String referenceNo;

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }
}
