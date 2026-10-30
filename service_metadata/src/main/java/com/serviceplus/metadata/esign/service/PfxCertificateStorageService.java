package com.serviceplus.metadata.esign.service;

import org.springframework.web.multipart.MultipartFile;

public interface PfxCertificateStorageService {

    PfxStorageResult storeCertificate(MultipartFile file, String tenantId, String pfxPassword);

    void validatePfxFile(MultipartFile file, String pfxPassword);

    byte[] retrieveCertificate(String storageReference, String tenantId);

    void deleteCertificate(String storageReference, String tenantId);

    String encryptPassword(String plainPassword);

    String decryptPassword(String encryptedPassword);
}
