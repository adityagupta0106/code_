package com.serviceplus.formDesigner.FormApiExecution.Interface;

import com.serviceplus.formDesigner.FormApiExecution.Model.ApiExecutionResponse;
import com.serviceplus.formDesigner.FormApiExecution.Model.FormApiServerActionRequest;
import reactor.core.publisher.Mono;

public interface FormApiExecutionService {

    Mono<ApiExecutionResponse> execute(FormApiServerActionRequest request);

}
