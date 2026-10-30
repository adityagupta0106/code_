package com.serviceplus.metadata.mvel.entity;

import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO;
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
@Table(name = "service_mvel_event_mapping_history", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class MvelEventMappingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false)
    private Long historyId;

    @Column(name = "modification_id", nullable = false)
    private Long modificationId;

    @Column(name = "archived_at", nullable = false)
    private Date archivedAt;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "mvel_event_mapped_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private MvelEventMappingDTO mvelEventMappingDTO;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "created_by")
    private Long createdBy;

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public MvelEventMappingDTO getMvelEventMappingDTO() {
        return mvelEventMappingDTO;
    }

    public void setMvelEventMappingDTO(MvelEventMappingDTO mvelEventMappingDTO) {
        this.mvelEventMappingDTO = mvelEventMappingDTO;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
