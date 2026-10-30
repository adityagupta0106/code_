package com.serviceplus.metadata.correlationConfig.entiry;

import com.serviceplus.metadata.correlationConfig.dto.ConfigureDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
@Table(name = "correlation_set_config_history", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class CorrelationConfigurationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false)
    private Long historyId;

    @Column(name = "modification_id", nullable = false)
    private Long modificationId;

    @Column(name = "archived_at", nullable = false)
    private Date archivedAt;

    @Column(name = "set_id")
    private Integer id;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "set_name", nullable = false)
    private String setName;

    @Column(name = "security_key")
    private String securityKey;

    @Column(name = "attr_config_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private ConfigureDTO config;

    @Column(name = "set_type")
    private String setType;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "cr_by")
    private Long crBy;

    @Column(name = "cr_date")
    private Date crDate;

    @Column(name = "up_date")
    private Date upDate;

    @Column(name = "up_by")
    private Long upBy;

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getModificationId() {
        return modificationId;
    }

    public void setModificationId(Long modificationId) {
        this.modificationId = modificationId;
    }

    public Date getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Date archivedAt) {
        this.archivedAt = archivedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public ConfigureDTO getConfig() {
        return config;
    }

    public void setConfig(ConfigureDTO config) {
        this.config = config;
    }

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getCrBy() {
        return crBy;
    }

    public void setCrBy(Long crBy) {
        this.crBy = crBy;
    }

    public Date getCrDate() {
        return crDate;
    }

    public void setCrDate(Date crDate) {
        this.crDate = crDate;
    }

    public Date getUpDate() {
        return upDate;
    }

    public void setUpDate(Date upDate) {
        this.upDate = upDate;
    }

    public Long getUpBy() {
        return upBy;
    }

    public void setUpBy(Long upBy) {
        this.upBy = upBy;
    }
}
