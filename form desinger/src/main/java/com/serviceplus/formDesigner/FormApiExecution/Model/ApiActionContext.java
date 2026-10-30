package com.serviceplus.formDesigner.FormApiExecution.Model;

import com.serviceplus.formDesigner.dto.ApiActionDefinition;

public class ApiActionContext {

    private String ownerAttributeId;

    private boolean globalAction;

    private ApiActionDefinition action;

    public String getOwnerAttributeId() {
        return ownerAttributeId;
    }

    public void setOwnerAttributeId(String ownerAttributeId) {
        this.ownerAttributeId = ownerAttributeId;
    }

    public boolean isGlobalAction() {
        return globalAction;
    }

    public void setGlobalAction(boolean globalAction) {
        this.globalAction = globalAction;
    }

    public ApiActionDefinition getAction() {
        return action;
    }

    public void setAction(ApiActionDefinition action) {
        this.action = action;
    }
}
