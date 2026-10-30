
package com.serviceplus.metadata.esign.entity;

import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.esign.dto.ServiceEsignConfigurationDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Table(
    name = "service_esign_configuration",
    schema = ApplicationConstants.SP_SCHEMA_NAME,
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_service_esign_config_service_tenant",
            columnNames = {"service_id", "tenant_id"}
        )
    }
)
public class ServiceEsignConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "esign_enabled", nullable = false)
    private Boolean esignEnabled = Boolean.FALSE;

    @Column(name = "esign_template_id")
    private Long esignTemplateId;

    @Column(
        name = "service_esign_json",
        nullable = false,
        columnDefinition = "jsonb"
    )
    @JdbcTypeCode(SqlTypes.JSON)
    private ServiceEsignConfigurationDTO serviceEsignJson;

    @Column(name = "cr_by", nullable = false)
    private Long createdBy;

    @Column(name = "cr_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "up_by")
    private Long updatedBy;

    @Column(name = "up_date")
    private LocalDateTime updatedDate;

    public ServiceEsignConfiguration() {
        this.serviceEsignJson = new ServiceEsignConfigurationDTO();
        this.serviceEsignJson.setEsignEnabled(Boolean.FALSE);
    }

    /**
     * Ensures the JSON configuration DTO exists before delegating
     * any configuration property to it.
     */
    private ServiceEsignConfigurationDTO ensureServiceEsignJson() {
        if (this.serviceEsignJson == null) {
            this.serviceEsignJson = new ServiceEsignConfigurationDTO();
        }
        return this.serviceEsignJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        ensureServiceEsignJson().setId(id);
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
        ensureServiceEsignJson().setServiceId(serviceId);
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getEsignEnabled() {
        return esignEnabled;
    }

    public void setEsignEnabled(Boolean esignEnabled) {
        this.esignEnabled =
                esignEnabled != null ? esignEnabled : Boolean.FALSE;

        ensureServiceEsignJson().setEsignEnabled(this.esignEnabled);
    }

    public Long getEsignTemplateId() {
        return esignTemplateId;
    }

    public void setEsignTemplateId(Long esignTemplateId) {
        this.esignTemplateId = esignTemplateId;
        ensureServiceEsignJson().setEsignTemplateId(esignTemplateId);
    }

    public ServiceEsignConfigurationDTO getServiceEsignJson() {
        return serviceEsignJson;
    }

    public void setServiceEsignJson(
            ServiceEsignConfigurationDTO serviceEsignJson) {

        this.serviceEsignJson = serviceEsignJson != null
                ? serviceEsignJson
                : new ServiceEsignConfigurationDTO();

        if (this.serviceEsignJson.getId() != null) {
            this.id = this.serviceEsignJson.getId();
        }

        if (this.serviceEsignJson.getServiceId() != null) {
            this.serviceId = this.serviceEsignJson.getServiceId();
        }

        if (this.serviceEsignJson.getEsignEnabled() != null) {
            this.esignEnabled = this.serviceEsignJson.getEsignEnabled();
        }

        if (this.serviceEsignJson.getEsignTemplateId() != null) {
            this.esignTemplateId =
                    this.serviceEsignJson.getEsignTemplateId();
        }
    }

    // ----------------------------------------------------------------
    // JSONB configuration delegation
    // These methods preserve LabelValue objects without flattening them
    // into comma-separated strings.
    // ----------------------------------------------------------------

    public Boolean getPrintSignerName() {
        return serviceEsignJson != null
                && serviceEsignJson.getPrintSignerName() != null
                ? serviceEsignJson.getPrintSignerName()
                : Boolean.FALSE;
    }

    public void setPrintSignerName(Boolean printSignerName) {
        ensureServiceEsignJson().setPrintSignerName(printSignerName);
    }

    public Boolean getBiometricEnabled() {
        return serviceEsignJson != null
                && serviceEsignJson.getBiometricEnabled() != null
                ? serviceEsignJson.getBiometricEnabled()
                : Boolean.FALSE;
    }

    public void setBiometricEnabled(Boolean biometricEnabled) {
        ensureServiceEsignJson().setBiometricEnabled(biometricEnabled);
    }

    public List<LabelValue> getSubmissionModes() {
        return serviceEsignJson != null
                && serviceEsignJson.getSubmissionModes() != null
                ? serviceEsignJson.getSubmissionModes()
                : Collections.emptyList();
    }

    public void setSubmissionModes(List<LabelValue> submissionModes) {
        ensureServiceEsignJson().setSubmissionModes(
                submissionModes != null
                        ? submissionModes
                        : Collections.emptyList()
        );
    }

    public List<LabelValue> getIdentifiedUserGroupTypes() {
        return serviceEsignJson != null
                && serviceEsignJson.getIdentifiedUserGroupTypes() != null
                ? serviceEsignJson.getIdentifiedUserGroupTypes()
                : Collections.emptyList();
    }

    public void setIdentifiedUserGroupTypes(
            List<LabelValue> identifiedUserGroupTypes) {

        ensureServiceEsignJson().setIdentifiedUserGroupTypes(
                identifiedUserGroupTypes != null
                        ? identifiedUserGroupTypes
                        : Collections.emptyList()
        );
    }

    public List<LabelValue> getBiometricTypes() {
        return serviceEsignJson != null
                && serviceEsignJson.getBiometricTypes() != null
                ? serviceEsignJson.getBiometricTypes()
                : Collections.emptyList();
    }

    public void setBiometricTypes(List<LabelValue> biometricTypes) {
        ensureServiceEsignJson().setBiometricTypes(
                biometricTypes != null
                        ? biometricTypes
                        : Collections.emptyList()
        );
    }

    public Boolean getShareAadhaar() {
        return serviceEsignJson != null
                && serviceEsignJson.getShareAadhaar() != null
                ? serviceEsignJson.getShareAadhaar()
                : Boolean.FALSE;
    }

    public void setShareAadhaar(Boolean shareAadhaar) {
        ensureServiceEsignJson().setShareAadhaar(shareAadhaar);
    }

    public LabelValue getTaskId() {
        return serviceEsignJson != null
                ? serviceEsignJson.getTaskId()
                : null;
    }

    public void setTaskId(LabelValue taskId) {
        ensureServiceEsignJson().setTaskId(taskId);
    }

    public LabelValue getAttributeId() {
        return serviceEsignJson != null
                ? serviceEsignJson.getAttributeId()
                : null;
    }

    public void setAttributeId(LabelValue attributeId) {
        ensureServiceEsignJson().setAttributeId(attributeId);
    }

    public Boolean getVerifySignerName() {
        return serviceEsignJson != null
                && serviceEsignJson.getVerifySignerName() != null
                ? serviceEsignJson.getVerifySignerName()
                : Boolean.FALSE;
    }

    public void setVerifySignerName(Boolean verifySignerName) {
        ensureServiceEsignJson().setVerifySignerName(verifySignerName);
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
}
