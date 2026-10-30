package com.serviceplus.metadata.correlationmapping.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.correlationmapping.dto.CombinationSetDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "external_data_combination",
    schema = ApplicationConstants.SP_SCHEMA_NAME
)
public class ExternalDataCombination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "combination_name", nullable = false)
    private String combinationName;

    @Column(name = "search_after_data_found", nullable = false)
    private Boolean searchOnDataFound;

    @Column(name = "sets_json", columnDefinition = "json", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<CombinationSetDTO> sets;

    @Column(name = "cr_date")
    private Date crDate;

    @Column(name = "cr_by")
    private Long crBy;

    @Column(name = "up_date")
    private Date upDate;

    @Column(name = "up_by")
    private Long upBy;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "entity_id")
    private Integer entityId;

    @PrePersist
    protected void onCreate() {

        Date now = new Date();

        crDate = now;
        upDate = now;
    }

    @PreUpdate
    protected void onUpdate() {

        upDate = new Date();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCombinationName() {
		return combinationName;
	}

	public void setCombinationName(String combinationName) {
		this.combinationName = combinationName;
	}

	public Boolean getSearchOnDataFound() {
		return searchOnDataFound;
	}

	public void setSearchOnDataFound(Boolean searchOnDataFound) {
		this.searchOnDataFound = searchOnDataFound;
	}

	public List<CombinationSetDTO> getSets() {
		return sets;
	}

	public void setSets(List<CombinationSetDTO> sets) {
		this.sets = sets;
	}

	public Date getCrDate() {
		return crDate;
	}

	public void setCrDate(Date crDate) {
		this.crDate = crDate;
	}

	public Long getCrBy() {
		return crBy;
	}

	public void setCrBy(Long crBy) {
		this.crBy = crBy;
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

    
}
