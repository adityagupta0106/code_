package com.serviceplus.formDesigner.feignClient;


import com.serviceplus.formDesigner.FormApiExecution.Model.ApiExecutionRequest;
import com.serviceplus.formDesigner.FormApiExecution.Model.ApiExecutionResponse;
import com.serviceplus.formDesigner.FormApiExecution.Model.FormApiServerActionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "EXTERNALAPISERVICE", path = "/external")
public interface ExternalApiFeignClient {

    @PostMapping("/b/web-service/execute")
    ApiExecutionResponse execute(ApiExecutionRequest request);

}
