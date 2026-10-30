package com.serviceplus.metadata.esign.service;

import java.time.LocalDateTime;

public class PfxStorageResult {

    private String storageReference;
    private String originalFilename;
    private Long fileSize;
    private String sha256Fingerprint;
    private LocalDateTime expiryDate;
    private String subjectDn;
    private String issuerDn;
    private String encryptedPassword;

    public PfxStorageResult() {
    }

    public PfxStorageResult(String storageReference, String originalFilename, Long fileSize,
                            String sha256Fingerprint, LocalDateTime expiryDate,
                            String subjectDn, String issuerDn, String encryptedPassword) {
        this.storageReference = storageReference;
        this.originalFilename = originalFilename;
        this.fileSize = fileSize;
        this.sha256Fingerprint = sha256Fingerprint;
        this.expiryDate = expiryDate;
        this.subjectDn = subjectDn;
        this.issuerDn = issuerDn;
        this.encryptedPassword = encryptedPassword;
    }

    public String getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(String storageReference) {
        this.storageReference = storageReference;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getSha256Fingerprint() {
        return sha256Fingerprint;
    }

    public void setSha256Fingerprint(String sha256Fingerprint) {
        this.sha256Fingerprint = sha256Fingerprint;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSubjectDn() {
        return subjectDn;
    }

    public void setSubjectDn(String subjectDn) {
        this.subjectDn = subjectDn;
    }

    public String getIssuerDn() {
        return issuerDn;
    }

    public void setIssuerDn(String issuerDn) {
        this.issuerDn = issuerDn;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
