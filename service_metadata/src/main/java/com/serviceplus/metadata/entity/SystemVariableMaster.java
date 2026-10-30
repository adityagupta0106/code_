package com.serviceplus.metadata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_variable_master", schema = "schm_sp")
public class SystemVariableMaster {

    @Id
    @Column(name = "attribute_id", nullable = false)
    private String attributeId;

    @Column(name = "attribute_label")
    private String attributeLabel;

    @Column(name = "module_id")
    private Long moduleId;

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeLabel() {
        return attributeLabel;
    }

    public void setAttributeLabel(String attributeLabel) {
        this.attributeLabel = attributeLabel;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }
}
