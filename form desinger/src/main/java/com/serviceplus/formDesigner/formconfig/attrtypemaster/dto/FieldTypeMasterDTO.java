package com.serviceplus.formDesigner.formconfig.attrtypemaster.dto;

import java.util.List;
import java.util.Map;

public class FieldTypeMasterDTO {

    private Map<String, List<InputTypeItem>> groupMap;

    public Map<String, List<InputTypeItem>> getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(Map<String, List<InputTypeItem>> groupMap) {
        this.groupMap = groupMap;
    }

    public static class InputTypeItem {
        private Integer typeId;
        private String icon;
        private String label;
        private String attrType;

        public InputTypeItem(Integer typeId, String icon, String label, String attrType) {
            this.typeId = typeId;
            this.icon = icon;
            this.label = label;
            this.attrType = attrType;
        }

        public Integer getTypeId() {
            return typeId;
        }

        public String getIcon() {
            return icon;
        }

        public String getLabel() {
            return label;
        }

        public String getAttrType() {
            return attrType;
        }
    }
}
