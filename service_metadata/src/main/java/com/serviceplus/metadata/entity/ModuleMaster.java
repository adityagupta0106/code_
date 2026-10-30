package com.serviceplus.metadata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "module_master", schema = "schm_sp")
public class ModuleMaster {

    @Id
    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(name = "status", nullable = false)
    private Boolean status;

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
