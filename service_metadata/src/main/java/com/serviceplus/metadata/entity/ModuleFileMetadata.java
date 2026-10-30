package com.serviceplus.metadata.entity;

import java.io.Serializable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "module_file_metadata", schema = "schm_sp")
public class ModuleFileMetadata {

    @EmbeddedId
    private ModuleFileMetadataId id;

    @Column(name = "description")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "master_data", columnDefinition = "jsonb")
    private String masterData;

    public ModuleFileMetadataId getId() {
        return id;
    }

    public void setId(ModuleFileMetadataId id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMasterData() {
        return masterData;
    }

    public void setMasterData(String masterData) {
        this.masterData = masterData;
    }

    @Embeddable
    public static class ModuleFileMetadataId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "module_id")
        private Long moduleId;

        @Column(name = "reference_id")
        private String referenceId;

        public Long getModuleId() {
            return moduleId;
        }

        public void setModuleId(Long moduleId) {
            this.moduleId = moduleId;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((moduleId == null) ? 0 : moduleId.hashCode());
            result = prime * result + ((referenceId == null) ? 0 : referenceId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;

            ModuleFileMetadataId other = (ModuleFileMetadataId) obj;

            if (moduleId == null) {
                if (other.moduleId != null)
                    return false;
            } else if (!moduleId.equals(other.moduleId))
                return false;

            if (referenceId == null) {
                return other.referenceId == null;
            } else {
                return referenceId.equals(other.referenceId);
            }
        }
    }
}
