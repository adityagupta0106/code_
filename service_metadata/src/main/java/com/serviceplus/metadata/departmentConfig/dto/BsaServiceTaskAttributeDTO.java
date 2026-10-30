package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BsaServiceTaskAttributeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer serviceId;
    private List<TaskOption> tasks = new ArrayList<>();

    public BsaServiceTaskAttributeDTO() {
    }

    public BsaServiceTaskAttributeDTO(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public List<TaskOption> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskOption> tasks) {
        this.tasks = tasks;
    }

    public static class TaskOption implements Serializable {
        private static final long serialVersionUID = 1L;

        private String taskId;
        private String taskName;
        private String formId;
        private List<AttributeOption> attributes = new ArrayList<>();

        public TaskOption() {
        }

        public TaskOption(String taskId, String taskName, String formId) {
            this.taskId = taskId;
            this.taskName = taskName;
            this.formId = formId;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getFormId() {
            return formId;
        }

        public void setFormId(String formId) {
            this.formId = formId;
        }

        public List<AttributeOption> getAttributes() {
            return attributes;
        }

        public void setAttributes(List<AttributeOption> attributes) {
            this.attributes = attributes;
        }
    }

    public static class AttributeOption implements Serializable {
        private static final long serialVersionUID = 1L;

        private String attributeId;
        private String attributeLabel;
        private String type;

        public AttributeOption() {
        }

        public AttributeOption(String attributeId, String attributeLabel, String type) {
            this.attributeId = attributeId;
            this.attributeLabel = attributeLabel;
            this.type = type;
        }

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
