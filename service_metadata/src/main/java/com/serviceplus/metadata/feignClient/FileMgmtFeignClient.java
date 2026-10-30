package com.serviceplus.metadata.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.serviceplus.metadata.dto.Base64UploadRequest;
import com.serviceplus.metadata.dto.CommitRequest;
import com.serviceplus.metadata.dto.CommitResponse;
import com.serviceplus.metadata.dto.CreateUploadSessionsRequest;
import com.serviceplus.metadata.dto.CreateUploadSessionsResponse;
import com.serviceplus.metadata.dto.DSCSignRequest;
import com.serviceplus.metadata.dto.DSCSignResponse;
import com.serviceplus.metadata.dto.FileViewResponse;
import com.serviceplus.metadata.dto.UploadStatusResponse;

@FeignClient(name = "FILEMANAGEMENT", path = "/filemgmt")
public interface FileMgmtFeignClient {
	@PostMapping("/b/upload-sessions")
	ResponseEntity<CreateUploadSessionsResponse> generateCreateUploadSessionsResponse(@RequestHeader("USER-DETAILS") String userDetails,@RequestBody CreateUploadSessionsRequest createUploadSessionsRequest);

    @PostMapping("/b/files/commit")
    ResponseEntity<CommitResponse> commitFiles(@RequestHeader("USER-DETAILS") String userDetails, @RequestBody CommitRequest commitRequest);

    @PostMapping("/b/view/batch")
    ResponseEntity<FileViewResponse> filePreview(@RequestHeader("USER-DETAILS") String userDetails, @RequestBody CommitRequest commitRequest);
    
	@PostMapping("/a/dsc/sign")
	ResponseEntity<DSCSignResponse> signDocument(@RequestHeader("USER-DETAILS") String userDetails,@RequestBody DSCSignRequest request);
		
	@PostMapping(value = "/b/upload/base64")
	ResponseEntity<UploadStatusResponse> uploadBase64File(@RequestHeader("USER-DETAILS") String userDetails,@RequestHeader("X-Upload-Id") String uploadId,
			@RequestHeader("Authorization") String uploadToken ,@RequestBody Base64UploadRequest request);
    
}
