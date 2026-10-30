package com.serviceplus.metadata.dto;

import java.io.Serializable;

public class LgdUnitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long parentEntityUnitCode;
    private Long parentEntityCode;
    private Long childEntityUnitCode;
    private Long childEntityCode;
    private String parentEntityUnitName;
    private String childEntityUnitName;
    private String adoptionStatus;

    public Long getParentEntityUnitCode() {
        return parentEntityUnitCode;
    }

    public void setParentEntityUnitCode(Long parentEntityUnitCode) {
        this.parentEntityUnitCode = parentEntityUnitCode;
    }

    public Long getParentEntityCode() {
        return parentEntityCode;
    }

    public void setParentEntityCode(Long parentEntityCode) {
        this.parentEntityCode = parentEntityCode;
    }

    public Long getChildEntityUnitCode() {
        return childEntityUnitCode;
    }

    public void setChildEntityUnitCode(Long childEntityUnitCode) {
        this.childEntityUnitCode = childEntityUnitCode;
    }

    public Long getChildEntityCode() {
        return childEntityCode;
    }

    public void setChildEntityCode(Long childEntityCode) {
        this.childEntityCode = childEntityCode;
    }

    public String getParentEntityUnitName() {
        return parentEntityUnitName;
    }

    public void setParentEntityUnitName(String parentEntityUnitName) {
        this.parentEntityUnitName = parentEntityUnitName;
    }

    public String getChildEntityUnitName() {
        return childEntityUnitName;
    }

    public void setChildEntityUnitName(String childEntityUnitName) {
        this.childEntityUnitName = childEntityUnitName;
    }

    public String getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(String adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }
}
