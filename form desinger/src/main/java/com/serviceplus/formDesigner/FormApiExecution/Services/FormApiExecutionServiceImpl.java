package com.serviceplus.formDesigner.FormApiExecution.Services;

import com.serviceplus.formDesigner.FormApiExecution.Interface.ApiActionResolver;
import com.serviceplus.formDesigner.FormApiExecution.Interface.FormApiExecutionService;
import com.serviceplus.formDesigner.FormApiExecution.Model.ApiActionContext;
import com.serviceplus.formDesigner.FormApiExecution.Model.ApiExecutionRequest;
import com.serviceplus.formDesigner.FormApiExecution.Model.ApiExecutionResponse;
import com.serviceplus.formDesigner.FormApiExecution.Model.FormApiServerActionRequest;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.feignClient.ExternalApiFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FormApiExecutionServiceImpl implements FormApiExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger("webServiceCallLogger");

    private final ApiActionResolver apiActionResolver;
    private final ExternalApiFeignClient externalApiFeignClient;

    public FormApiExecutionServiceImpl(ApiActionResolver apiActionResolver, ExternalApiFeignClient externalApiFeignClient) {
        this.apiActionResolver = apiActionResolver;
        this.externalApiFeignClient = externalApiFeignClient;
    }

    @Override
    public Mono<ApiExecutionResponse> execute(FormApiServerActionRequest request) {

        LOGGER.info("Executing API [{}] for form [{}]", request.getApiId(), request.getFormId());

        ApiActionContext context = apiActionResolver.resolve(request.getFormId(), request.getApiId());

        if (context == null) {
            throw new SPRuntimeError("API configuration not found.", HttpStatus.BAD_REQUEST);
        }

        ApiExecutionRequest apiExecutionRequest = new ApiExecutionRequest();
        apiExecutionRequest.setApiId(request.getApiId());
        apiExecutionRequest.setApiDefinition(context.getAction());
        apiExecutionRequest.setAttributeValues(request.getAttributeValues());
        apiExecutionRequest.setValidationRequired(Boolean.TRUE);

        ApiExecutionResponse response = externalApiFeignClient.execute(apiExecutionRequest);

        return Mono.just(response);
    }
}