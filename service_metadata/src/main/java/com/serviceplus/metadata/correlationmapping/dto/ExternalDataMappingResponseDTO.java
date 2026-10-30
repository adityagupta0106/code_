package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

import com.serviceplus.metadata.dto.LabelValue;

public class ExternalDataMappingResponseDTO {

    private Integer id;

    private List<LabelValue> source = new ArrayList<>();

    private LabelValue serviceStatus;

    private DataSetSourcesResponseDTO dataSetSources;

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

    public DataSetSourcesResponseDTO getDataSetSources() {
        return dataSetSources;
    }

    public void setDataSetSources(DataSetSourcesResponseDTO dataSetSources) {
        this.dataSetSources = dataSetSources;
    }

    public static class DataSetSourcesResponseDTO {

        private List<ServiceDataSetResponseDTO> service = new ArrayList<>();

        private List<ExternalDataSetResponseDTO> external = new ArrayList<>();

        public List<ServiceDataSetResponseDTO> getService() {
            return service;
        }

        public void setService(List<ServiceDataSetResponseDTO> service) {
            this.service = service;
        }

        public List<ExternalDataSetResponseDTO> getExternal() {
            return external;
        }

        public void setExternal(List<ExternalDataSetResponseDTO> external) {
            this.external = external;
        }
    }

    public static class ServiceDataSetResponseDTO {

        private Long serviceId;

        private Long baseServiceId;

        private String serviceName;

        private String sourceType;

        private String status;

        private String remark;

        private List<ApprovedSetResponseDTO> approvedSets = new ArrayList<>();

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public List<ApprovedSetResponseDTO> getApprovedSets() {
            return approvedSets;
        }

        public void setApprovedSets(
                List<ApprovedSetResponseDTO> approvedSets) {
            this.approvedSets = approvedSets;
        }
    }

    public static class ExternalDataSetResponseDTO {

        private Long setId;

        private String setName;

        private String sourceType;

        private String status;

        private String remark;

        private List<ApprovedSetResponseDTO> approvedSets = new ArrayList<>();

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public List<ApprovedSetResponseDTO> getApprovedSets() {
            return approvedSets;
        }

        public void setApprovedSets(
                List<ApprovedSetResponseDTO> approvedSets) {
            this.approvedSets = approvedSets;
        }
    }


    public static class ApprovedSetResponseDTO {

        private Long setId;

        private String setName;

        private List<OutputSetResponseDTO> outputSet = new ArrayList<>();

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

        public List<OutputSetResponseDTO> getOutputSet() {
            return outputSet;
        }

        public void setOutputSet(
                List<OutputSetResponseDTO> outputSet) {
            this.outputSet = outputSet;
        }
    }

    public static class OutputSetResponseDTO {

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