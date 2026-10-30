package com.serviceplus.formDesigner.feignClient;

import com.serviceplus.formDesigner.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "FILEMANAGEMENT", path = "/filemgmt")
public interface FileMgmtFeignClient {
	@PostMapping("/b/upload-sessions")
	ResponseEntity<CreateUploadSessionsResponse> generateCreateUploadSessionsResponse(@RequestHeader("USER-DETAILS") String userDetails,@RequestBody CreateUploadSessionsRequest createUploadSessionsRequest);

    @PostMapping("/b/files/commit")
    ResponseEntity<CommitResponse> commitFiles(@RequestHeader("USER-DETAILS") String userDetails, @RequestBody CommitRequest commitRequest);

    @PostMapping("/b/view/batch")
    ResponseEntity<FileViewResponse> filePreview(@RequestHeader("USER-DETAILS") String userDetails, @RequestBody CommitRequest commitRequest);
}
