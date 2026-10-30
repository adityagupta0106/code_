package com.serviceplus.tracking.feignClient;


import com.serviceplus.tracking.dto.SignedUrlResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "FILEMANAGEMENT", path = "/filemgmt", url = "${filemanagement.service.url}")
public interface FileManagementFeignClient {

    @GetMapping("/b/{uploadId}/download")
    SignedUrlResponse download(@PathVariable("uploadId") String uploadId, @RequestHeader Map<String, Object> headers);

}
