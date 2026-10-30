package com.serviceplus.metadata.dto;

public class HierarchyLabelValue extends LabelValue {

    private LabelValue category;
    private LabelValue entity;
    private LabelValue clcDetail;
    private LabelValue entityLevel;

    public LabelValue getCategory() {
        return category;
    }

    public void setCategory(LabelValue category) {
        this.category = category;
    }

    public LabelValue getEntity() {
        return entity;
    }

    public void setEntity(LabelValue entity) {
        this.entity = entity;
    }

    public LabelValue getClcDetail() {
        return clcDetail;
    }

    public void setClcDetail(LabelValue clcDetail) {
        this.clcDetail = clcDetail;
    }

    public LabelValue getEntityLevel() {
        return entityLevel;
    }

    public void setEntityLevel(LabelValue entityLevel) {
        this.entityLevel = entityLevel;
    }
}
