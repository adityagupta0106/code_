package com.serviceplus.metadata.esign.entity;

import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.esign.dto.EsignTemplateJSONDTO;
import com.serviceplus.metadata.esign.enums.EsignTemplateStatus;
import com.serviceplus.metadata.utility.ApplicationConstants;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "esign_template", schema = ApplicationConstants.SP_SCHEMA_NAME,
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_esign_template_name_tenant", columnNames = {"tenant_id", "template_name"})
        })
public class EsignTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EsignTemplateStatus status = EsignTemplateStatus.ACTIVE;

    @Column(name = "template_json", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private EsignTemplateJSONDTO templateJson;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public EsignTemplate() {
        this.templateJson = new EsignTemplateJSONDTO();
    }

    private EsignTemplateJSONDTO ensureTemplateJson() {
        if (this.templateJson == null) {
            this.templateJson = new EsignTemplateJSONDTO();
        }
        return this.templateJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public EsignTemplateStatus getStatus() {
        return status;
    }

    public void setStatus(EsignTemplateStatus status) {
        this.status = status;
    }

    public EsignTemplateJSONDTO getTemplateJson() {
        return templateJson;
    }

    public void setTemplateJson(EsignTemplateJSONDTO templateJson) {
        this.templateJson = templateJson;
    }

    // Delegation methods for jsonb fields
    public String getAspId() {
        return templateJson != null ? templateJson.getAspId() : null;
    }

    public void setAspId(String aspId) {
        ensureTemplateJson().setAspId(aspId);
    }

    public Boolean getHasAppId() {
        return templateJson != null && templateJson.getHasAppId() != null ? templateJson.getHasAppId() : false;
    }

    public void setHasAppId(Boolean hasAppId) {
        ensureTemplateJson().setHasAppId(hasAppId);
    }

    public String getAppId() {
        return templateJson != null ? templateJson.getAppId() : null;
    }

    public void setAppId(String appId) {
        ensureTemplateJson().setAppId(appId);
    }

    public String getPfxStorageReference() {
        return templateJson != null ? templateJson.getPfxStorageReference() : null;
    }

    public void setPfxStorageReference(String pfxStorageReference) {
        ensureTemplateJson().setPfxStorageReference(pfxStorageReference);
    }

    public String getPfxFilename() {
        return templateJson != null ? templateJson.getPfxFilename() : null;
    }

    public void setPfxFilename(String pfxFilename) {
        ensureTemplateJson().setPfxFilename(pfxFilename);
    }

    public Long getPfxFileSize() {
        return templateJson != null ? templateJson.getPfxFileSize() : null;
    }

    public void setPfxFileSize(Long pfxFileSize) {
        ensureTemplateJson().setPfxFileSize(pfxFileSize);
    }

    public String getPfxFingerprint() {
        return templateJson != null ? templateJson.getPfxFingerprint() : null;
    }

    public void setPfxFingerprint(String pfxFingerprint) {
        ensureTemplateJson().setPfxFingerprint(pfxFingerprint);
    }

    public LocalDateTime getCertificateExpiryDate() {
        return templateJson != null ? templateJson.getCertificateExpiryDate() : null;
    }

    public void setCertificateExpiryDate(LocalDateTime certificateExpiryDate) {
        ensureTemplateJson().setCertificateExpiryDate(certificateExpiryDate);
    }

    public String getCertificateSubjectDn() {
        return templateJson != null ? templateJson.getCertificateSubjectDn() : null;
    }

    public void setCertificateSubjectDn(String certificateSubjectDn) {
        ensureTemplateJson().setCertificateSubjectDn(certificateSubjectDn);
    }

    public String getCertificateIssuerDn() {
        return templateJson != null ? templateJson.getCertificateIssuerDn() : null;
    }

    public void setCertificateIssuerDn(String certificateIssuerDn) {
        ensureTemplateJson().setCertificateIssuerDn(certificateIssuerDn);
    }

    public String getPfxPassword() {
        return templateJson != null ? templateJson.getPfxPassword() : null;
    }

    public void setPfxPassword(String encryptedPfxPassword) {
        ensureTemplateJson().setPfxPassword(encryptedPfxPassword);
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

	public void setTemplateName(LabelValue templateLabelVal) {
		ensureTemplateJson().setTemplateName(templateLabelVal);
	}
}
