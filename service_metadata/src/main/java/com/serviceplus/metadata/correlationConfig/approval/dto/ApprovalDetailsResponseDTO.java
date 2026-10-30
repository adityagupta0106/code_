package com.serviceplus.metadata.correlationConfig.approval.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApprovalDetailsResponseDTO {

    private Long setId;

    private String setName;

    private List<String> inputAttributes = new ArrayList<>();

    private List<OutputAttributeDTO> outputAttributes =
            new ArrayList<>();

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

    public List<String> getInputAttributes() {
        return inputAttributes;
    }

    public void setInputAttributes(
            List<String> inputAttributes) {
        this.inputAttributes = inputAttributes;
    }

    public List<OutputAttributeDTO> getOutputAttributes() {
        return outputAttributes;
    }

    public void setOutputAttributes(
            List<OutputAttributeDTO> outputAttributes) {
        this.outputAttributes = outputAttributes;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OutputAttributeDTO {

        private String name;

        private List<String> attributes = new ArrayList<>();

        private String outputSetid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getAttributes() {
            return attributes;
        }

        public void setAttributes(List<String> attributes) {
            this.attributes = attributes;
        }

        public String getOutputSetid() {
            return outputSetid;
        }

        public void setOutputSetid(String outputSetid) {
            this.outputSetid = outputSetid;
        }
    }
}