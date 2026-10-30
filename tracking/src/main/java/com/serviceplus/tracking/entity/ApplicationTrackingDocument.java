package com.serviceplus.tracking.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "application_tracking_document", schema = SP_SCHEMA_NAME)
public class ApplicationTrackingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "tenant_id")
    private String tenantId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "view_permission", columnDefinition = "jsonb")
    private List<String> viewPermission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", referencedColumnName = "process_id")
    private ApplicationTracking tracking;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public ApplicationTracking getTracking() {
        return tracking;
    }

    public void setTracking(ApplicationTracking tracking) {
        this.tracking = tracking;
    }

    public List<String> getViewPermission() {
        return viewPermission;
    }

    public void setViewPermission(List<String> viewPermission) {
        this.viewPermission = viewPermission;
    }
}
