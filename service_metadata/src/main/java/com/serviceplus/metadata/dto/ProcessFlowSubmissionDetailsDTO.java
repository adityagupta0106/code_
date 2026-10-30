package com.serviceplus.metadata.dto;

import java.util.List;

public class ProcessFlowSubmissionDetailsDTO {

    private List<LabelValue> mode;
    private List<LabelValue> kioskType;
    private List<LabelValue> identifiedUserSetIds;

    public List<LabelValue> getMode() {
        return mode;
    }

    public void setMode(List<LabelValue> mode) {
        this.mode = mode;
    }

    public List<LabelValue> getKioskType() {
        return kioskType;
    }

    public void setKioskType(List<LabelValue> kioskType) {
        this.kioskType = kioskType;
    }

    public List<LabelValue> getIdentifiedUserSetIds() {
        return identifiedUserSetIds;
    }

    public void setIdentifiedUserSetIds(
            List<LabelValue> identifiedUserSetIds) {
        this.identifiedUserSetIds = identifiedUserSetIds;
    }
}
