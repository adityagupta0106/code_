package com.serviceplus.metadata.aadhaarConfiguration.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AadhaarPayloadConfiguration implements Serializable {


    private String xsd;

    private String schemaName;

    private List<AadhaarNodeConfiguration> nodes = new ArrayList<>();


    public AadhaarPayloadConfiguration() {
    }


    public String getXsd() {
        return xsd;
    }

    public void setXsd(String xsd) {
        this.xsd = xsd;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public List<AadhaarNodeConfiguration> getNodes() {
        return nodes;
    }

    public void setNodes(
            List<AadhaarNodeConfiguration> nodes
    ) {
        this.nodes = nodes;
    }
}
