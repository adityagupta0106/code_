package com.serviceplus.metadata.aadhaarConfiguration.dto;

import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarAuthenticationType;
import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarConfigurationMode;
import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarConfigurationStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AadhaarConfiguration implements Serializable {

    @Id
    private String id;

    /*
     * Master configuration
     */
    private String auaName;

    private String configurationName;

    /*
     * AUTH / EKYC
     */
    private AadhaarAuthenticationType authenticationType;

    /*
     * OTP / DEMOGRAPHIC
     * Multiple modes may exist under AUTH.
     */
    private List<AadhaarConfigurationMode> enabledModes = new ArrayList<>();

    /*
     * Transaction ID configuration
     */
    private TransactionIdConfiguration transactionIdConfiguration;

    /*
     * API configurations
     */
    private List<AadhaarApiConfiguration> apiConfigurations = new ArrayList<>();

    /*
     * Configuration lifecycle
     */
    private AadhaarConfigurationStatus status;

    private Integer version;

    @CreatedDate
    private LocalDateTime crDate;

    @LastModifiedDate
    private LocalDateTime upDate;

    private Long crBy;

    private Long upBy;


    public AadhaarConfiguration() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuaName() {
        return auaName;
    }

    public void setAuaName(String auaName) {
        this.auaName = auaName;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public AadhaarAuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AadhaarAuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public List<AadhaarConfigurationMode> getEnabledModes() {
        return enabledModes;
    }

    public void setEnabledModes(List<AadhaarConfigurationMode> enabledModes) {
        this.enabledModes = enabledModes;
    }

    public TransactionIdConfiguration getTransactionIdConfiguration() {
        return transactionIdConfiguration;
    }

    public void setTransactionIdConfiguration(
            TransactionIdConfiguration transactionIdConfiguration
    ) {
        this.transactionIdConfiguration = transactionIdConfiguration;
    }

    public List<AadhaarApiConfiguration> getApiConfigurations() {
        return apiConfigurations;
    }

    public void setApiConfigurations(
            List<AadhaarApiConfiguration> apiConfigurations
    ) {
        this.apiConfigurations = apiConfigurations;
    }

    public AadhaarConfigurationStatus getStatus() {
        return status;
    }

    public void setStatus(AadhaarConfigurationStatus status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getCrBy() {
        return crBy;
    }

    public void setCrBy(Long crBy) {
        this.crBy = crBy;
    }

    public LocalDateTime getCrDate() {
        return crDate;
    }

    public void setCrDate(LocalDateTime crDate) {
        this.crDate = crDate;
    }

    public LocalDateTime getUpDate() {
        return upDate;
    }

    public void setUpDate(LocalDateTime upDate) {
        this.upDate = upDate;
    }

    public Long getUpBy() {
        return upBy;
    }

    public void setUpBy(Long upBy) {
        this.upBy = upBy;
    }
}
