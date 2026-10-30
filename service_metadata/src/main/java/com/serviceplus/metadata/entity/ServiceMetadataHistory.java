package com.serviceplus.metadata.entity;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.serviceplus.metadata.dto.ServiceJSONDTO;

@Document(collection = "service_metadata_history")
public class ServiceMetadataHistory {

    @Id
    @Field("service_id")
    private Integer serviceId;

    @Field("base_service_id")
    private Integer baseServiceId;

    @Field("service_json")
    private ServiceJSONDTO serviceJson;

    @Field("version_no")
    private Integer versionNo;

    @Field("minor_version_no")
    private Integer minorVersionNo;

    @Field("c_date")
    private Date createdOnDate;

    @Field("u_date")
    private Date updatedOnDate;
    
    @Field("tenant_id")
    private String tenantId;
    

    public ServiceMetadataHistory(ServiceLog serviceLog) {
		super();
		this.serviceId = serviceLog.getServiceId();
		this.baseServiceId = serviceLog.getBaseServiceId();
		this.versionNo = serviceLog.getVersionNo();
		this.minorVersionNo = serviceLog.getMinorVersionNo();
		this.tenantId=serviceLog.getTenantId();
		this.createdOnDate = new Date();
	}

	public ServiceMetadataHistory() {
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

    public ServiceJSONDTO getMetadataJson() {
        return serviceJson;
    }

    public void setMetadataJson(ServiceJSONDTO serviceJson) {
        this.serviceJson = serviceJson;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getMinorVersionNo() {
        return minorVersionNo;
    }

    public void setMinorVersionNo(Integer minorVersionNo) {
        this.minorVersionNo = minorVersionNo;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public Date getUpdatedOnDate() {
        return updatedOnDate;
    }

    public void setUpdatedOnDate(Date updatedOnDate) {
        this.updatedOnDate = updatedOnDate;
    }
}