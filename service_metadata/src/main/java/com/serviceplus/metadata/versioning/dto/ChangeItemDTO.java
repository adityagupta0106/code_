package com.serviceplus.metadata.versioning.dto;

public class ChangeItemDTO {

    private String category;
    private String item;
    private String property;
    private Object oldValue;
    private Object newValue;
    private String description;

    public ChangeItemDTO() {}

    public ChangeItemDTO(String category, String item, String property, Object oldValue, Object newValue, String description) {
        this.category = category;
        this.item = item;
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
