package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

import com.serviceplus.metadata.dto.LabelValue;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ExternalDataMappingRequestDTO {

    private Integer id;

    @NotNull(message = "source can't be null")
    @NotEmpty(message = "source can't be empty")
    @Valid
    private List<LabelValue> source;

    @NotNull(message = "serviceStatus can't be null")
    @Valid
    private LabelValue serviceStatus;

    @NotNull(message = "dataSetSources can't be null")
    @Valid
    private DataSetSourcesDTO dataSetSources;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<LabelValue> getSource() {
        return source;
    }

    public void setSource(List<LabelValue> source) {
        this.source = source;
    }

    public LabelValue getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(LabelValue serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public DataSetSourcesDTO getDataSetSources() {
        return dataSetSources;
    }

    public void setDataSetSources(DataSetSourcesDTO dataSetSources) {
        this.dataSetSources = dataSetSources;
    }

    public static class DataSetSourcesDTO {

        @Valid
        private List<ServiceDataSetDTO> service = new ArrayList<>();

        @Valid
        private List<ExternalDataSetDTO> external = new ArrayList<>();


        public List<ServiceDataSetDTO> getService() {
            return service;
        }

        public void setService(List<ServiceDataSetDTO> service) {
            this.service = service;
        }

        public List<ExternalDataSetDTO> getExternal() {
            return external;
        }

        public void setExternal(List<ExternalDataSetDTO> external) {
            this.external = external;
        }
    }

    public static class ServiceDataSetDTO {

        private Long serviceId;

        private Long baseServiceId;

        private String serviceName;

        private String sourceType;

        private Integer status;

        private Long approvedBy;

        private String remark;
        
        private String approvedOn;

        @Valid
        private List<ApprovedSetDTO> approvedSets = new ArrayList<>();


        public Long getServiceId() {
            return serviceId;
        }

        public void setServiceId(Long serviceId) {
            this.serviceId = serviceId;
        }

        public Long getBaseServiceId() {
            return baseServiceId;
        }

        public void setBaseServiceId(Long baseServiceId) {
            this.baseServiceId = baseServiceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Long getApprovedBy() {
            return approvedBy;
        }

        public void setApprovedBy(Long approvedBy) {
            this.approvedBy = approvedBy;
        }

        public String getApprovedOn() {
            return approvedOn;
        }

        public void setApprovedOn(String approvedOn) {
            this.approvedOn = approvedOn;
        }

        public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public List<ApprovedSetDTO> getApprovedSets() {
            return approvedSets;
        }

        public void setApprovedSets(List<ApprovedSetDTO> approvedSets) {
            this.approvedSets = approvedSets;
        }
    }

    public static class ExternalDataSetDTO {

        private Long setId;

        private String setName;

        private String sourceType;

        private Integer status;
        private String remark="";

        private Long approvedBy;

        private String approvedOn;

        @Valid
        private List<ApprovedSetDTO> approvedSets = new ArrayList<>();


        public Long getSetId() {
            return setId;
        }

        public void setSetId(Long setId) {
            this.setId = setId;
        }

        public String getSetName() {
            return setName;
        }

        public void setSetName(String setName) {
            this.setName = setName;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public Long getApprovedBy() {
            return approvedBy;
        }

        public void setApprovedBy(Long approvedBy) {
            this.approvedBy = approvedBy;
        }

        public String getApprovedOn() {
            return approvedOn;
        }

        public void setApprovedOn(String approvedOn) {
            this.approvedOn = approvedOn;
        }

        public List<ApprovedSetDTO> getApprovedSets() {
            return approvedSets;
        }

        public void setApprovedSets(List<ApprovedSetDTO> approvedSets) {
            this.approvedSets = approvedSets;
        }
    }


    public static class ApprovedSetDTO {

        private Long setId;

        private String setName;

        private List<OutputSetDTO> outputSet = new ArrayList<>();


        public Long getSetId() {
            return setId;
        }

        public void setSetId(Long setId) {
            this.setId = setId;
        }

        public String getSetName() {
            return setName;
        }

        public void setSetName(String setName) {
            this.setName = setName;
        }

        public List<OutputSetDTO> getOutputSet() {
            return outputSet;
        }

        public void setOutputSet(List<OutputSetDTO> outputSet) {
            this.outputSet = outputSet;
        }
    }

    public static class OutputSetDTO {

        private String id;

        private String name;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}