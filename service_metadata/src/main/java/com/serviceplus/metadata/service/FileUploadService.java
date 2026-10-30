package com.serviceplus.metadata.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.dto.CreateUploadSessionsRequest;
import com.serviceplus.metadata.dto.CreateUploadSessionsResponse;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ModuleFileMetadata;
import com.serviceplus.metadata.feignClient.FileMgmtFeignClient;
import com.serviceplus.metadata.repository.IModuleFileMetadataRepository;
import static com.serviceplus.metadata.utility.FileUploadUtil.setFileAttributesDTO;
import static com.serviceplus.metadata.utility.CommonUtil.entityToString;

@Service
public class FileUploadService {

    private final FileMgmtFeignClient fileMgmtFeignClient;
    private final IModuleFileMetadataRepository moduleFileMetadataRepository;

    public FileUploadService(FileMgmtFeignClient fileMgmtFeignClient,
            IModuleFileMetadataRepository moduleFileMetadataRepository) {
        this.fileMgmtFeignClient = fileMgmtFeignClient;
        this.moduleFileMetadataRepository = moduleFileMetadataRepository;
    }

    public ResponseEntity<CreateUploadSessionsResponse> getUploadIds(Long moduleId,UserSessionDTO userSessionDetails) throws Exception {

        List<ModuleFileMetadata> metadataList =moduleFileMetadataRepository.findByIdModuleId(moduleId);
        JSONArray formElementsArray = new JSONArray();
        for (ModuleFileMetadata metadata : metadataList) {
            JSONObject attribute = new JSONObject();
            attribute.put("referenceId",metadata.getId().getReferenceId());
            attribute.put("description",metadata.getDescription());
            if (metadata.getMasterData() != null) {
                JSONObject masterData =new JSONObject(metadata.getMasterData());
                for (String key : masterData.keySet()) {
                    attribute.put(key, masterData.get(key));
                }
            }
            formElementsArray.put(attribute);
        }
        CreateUploadSessionsRequest createRequest =new CreateUploadSessionsRequest();
        createRequest.setSourceService("metadata-service");
        createRequest.setUserId(userSessionDetails.getUserID());

        setFileAttributesDTO(formElementsArray,createRequest);
        String userSessionDetail=entityToString(userSessionDetails);
        ResponseEntity<CreateUploadSessionsResponse> response =fileMgmtFeignClient.generateCreateUploadSessionsResponse(userSessionDetail,createRequest);

        return ResponseEntity.ok(response.getBody());
    }
}
