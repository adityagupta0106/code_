package com.serviceplus.metadata.dto;

public class SignedUrlResponse {

    private String url;
    private Long expiryInSecond;

    public SignedUrlResponse(String url, long expiry) {
        this.url = url;
        this.expiryInSecond = expiry;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getExpiry() {
        return expiryInSecond;
    }

    public void setExpiry(Long expiry) {
        this.expiryInSecond = expiry;
    }
}
