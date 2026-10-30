package com.serviceplus.formDesigner.FormApiExecution.Controller;

import com.serviceplus.formDesigner.FormApiExecution.Interface.FormApiExecutionService;
import com.serviceplus.formDesigner.FormApiExecution.Model.ApiExecutionResponse;
import com.serviceplus.formDesigner.FormApiExecution.Model.FormApiServerActionRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/external-api")
public class FormApiExecutionController {

    private final FormApiExecutionService executionService;

    public FormApiExecutionController(FormApiExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/execute")
    public Mono<ApiExecutionResponse> execute(@RequestBody FormApiServerActionRequest request) {
        return executionService.execute(request);

    }

}
