package com.serviceplus.metadata.esign.service.impl;

import com.github.f4b6a3.ulid.UlidCreator;
import com.serviceplus.metadata.esign.service.PfxCertificateStorageService;
import com.serviceplus.metadata.esign.service.PfxStorageResult;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.utility.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.HexFormat;

@Service
public class SecurePfxCertificateStorageServiceImpl implements PfxCertificateStorageService {

    private static final Logger log = LoggerFactory.getLogger("esignLogger");
    private static final long MAX_PFX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private final String pfxStorageBaseDir;
    private final String aesServiceKey;

    public SecurePfxCertificateStorageServiceImpl(
            @Value("${serviceplus.esign.pfx-storage-path:#{systemProperties['java.io.tmpdir'] + '/serviceplus_esign_pfx'}}") String pfxStorageBaseDir,
            @Value("${aesServiceKey:e3nyflxiz86jb9ws}") String aesServiceKey) {
        this.pfxStorageBaseDir = pfxStorageBaseDir;
        this.aesServiceKey = aesServiceKey;
    }

    @Override
    public PfxStorageResult storeCertificate(MultipartFile file, String tenantId, String pfxPassword) {
        if (file == null || file.isEmpty()) {
            throw new SPRuntimeError("PFX certificate file is mandatory", HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > MAX_PFX_FILE_SIZE) {
            throw new SPRuntimeError("PFX certificate file size exceeds the maximum permitted limit (5 MB)", HttpStatus.BAD_REQUEST);
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.error("Failed to read uploaded PFX file bytes", e);
            throw new SPRuntimeError("Failed to process uploaded certificate file", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Validate PKCS#12 structure and extract metadata
        PfxParsedMetadata metadata = parseAndValidatePkcs12(bytes, pfxPassword);

        String storageRef = "PFX_" + UlidCreator.getMonotonicUlid().toString();

        try {
            Path tenantDir = Paths.get(pfxStorageBaseDir, sanitizeTenantId(tenantId));
            Files.createDirectories(tenantDir);

            Path targetPath = tenantDir.resolve(storageRef + ".pfx");
            try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                fos.write(bytes);
            }
        } catch (IOException e) {
            log.error("Failed to persist certificate securely to storage vault", e);
            throw new SPRuntimeError("Failed to store certificate in secure vault", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String encryptedPassword = null;
        if (pfxPassword != null && !pfxPassword.trim().isEmpty()) {
            encryptedPassword = encryptPassword(pfxPassword);
        }

        return new PfxStorageResult(
                storageRef,
                file.getOriginalFilename(),
                file.getSize(),
                metadata.fingerprint(),
                metadata.expiryDate(),
                metadata.subjectDn(),
                metadata.issuerDn(),
                encryptedPassword
        );
    }

    @Override
    public void validatePfxFile(MultipartFile file, String pfxPassword) {
        if (file == null || file.isEmpty()) {
            throw new SPRuntimeError("PFX certificate file is mandatory", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_PFX_FILE_SIZE) {
            throw new SPRuntimeError("PFX certificate file size exceeds the maximum permitted limit (5 MB)", HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] bytes = file.getBytes();
            parseAndValidatePkcs12(bytes, pfxPassword);
        } catch (IOException e) {
            throw new SPRuntimeError("Failed to read certificate bytes", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public byte[] retrieveCertificate(String storageReference, String tenantId) {
        if (storageReference == null || storageReference.trim().isEmpty()) {
            throw new SPRuntimeError("Storage reference is required", HttpStatus.BAD_REQUEST);
        }
        try {
            Path filePath = Paths.get(pfxStorageBaseDir, sanitizeTenantId(tenantId), storageReference + ".pfx");
            if (!Files.exists(filePath)) {
                throw new SPRuntimeError("Certificate file not found in secure vault", HttpStatus.NOT_FOUND);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Error retrieving certificate for ref: {}", storageReference, e);
            throw new SPRuntimeError("Error reading certificate file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteCertificate(String storageReference, String tenantId) {
        if (storageReference != null && !storageReference.trim().isEmpty()) {
            try {
                Path filePath = Paths.get(pfxStorageBaseDir, sanitizeTenantId(tenantId), storageReference + ".pfx");
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                log.warn("Could not delete stored certificate: {}", storageReference, e);
            }
        }
    }

    @Override
    public String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }
        return CommonUtil.AESEncrypt(plainPassword, aesServiceKey);
    }

    @Override
    public String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            return null;
        }
        return CommonUtil.AESDecrypt(encryptedPassword, aesServiceKey);
    }

    private PfxParsedMetadata parseAndValidatePkcs12(byte[] bytes, String pfxPassword) {
        if (bytes == null || bytes.length < 4) {
            throw new SPRuntimeError("Uploaded file is empty or corrupted", HttpStatus.BAD_REQUEST);
        }

        // PKCS#12 binary format begins with ASN.1 SEQUENCE header (tag 0x30)
        if (bytes[0] != 0x30) {
            throw new SPRuntimeError("Uploaded file is not a valid PFX/PKCS#12 certificate", HttpStatus.BAD_REQUEST);
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] passwordChars = (pfxPassword != null) ? pfxPassword.toCharArray() : new char[0];
            keyStore.load(new ByteArrayInputStream(bytes), passwordChars);

            String subjectDn = null;
            String issuerDn = null;
            LocalDateTime expiryDate = null;
            String fingerprint = null;

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keyStore.getCertificate(alias);
                if (cert instanceof X509Certificate x509) {
                    subjectDn = x509.getSubjectX500Principal().getName();
                    issuerDn = x509.getIssuerX500Principal().getName();
                    expiryDate = x509.getNotAfter().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    byte[] digest = md.digest(x509.getEncoded());
                    fingerprint = HexFormat.of().formatHex(digest);
                    break;
                }
            }

            return new PfxParsedMetadata(fingerprint, expiryDate, subjectDn, issuerDn);
        } catch (Exception e) {
            log.error("PFX certificate validation failed: {}", e.getMessage());
            throw new SPRuntimeError("Invalid PFX certificate or incorrect password", HttpStatus.BAD_REQUEST);
        }
    }

    private String sanitizeTenantId(String tenantId) {
        if (tenantId == null) {
            return "default";
        }
        return tenantId.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private record PfxParsedMetadata(
            String fingerprint,
            LocalDateTime expiryDate,
            String subjectDn,
            String issuerDn
    ) {}
}
