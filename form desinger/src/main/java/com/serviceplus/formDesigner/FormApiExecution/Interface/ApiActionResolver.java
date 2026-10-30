package com.serviceplus.formDesigner.FormApiExecution.Interface;

import com.serviceplus.formDesigner.FormApiExecution.Model.ApiActionContext;

public interface ApiActionResolver {

    ApiActionContext resolve(String formId, String apiId);
}
