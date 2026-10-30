package com.serviceplus.metadata.utility;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.serviceplus.metadata.dto.CreateUploadSessionsRequest;
import com.serviceplus.metadata.dto.CreateUploadSessionsRequest.FileUploadRequest;

public class FileUploadUtil {

	public static void setFileAttributesDTO(JSONArray formElementsArray,
			CreateUploadSessionsRequest createUploadSessionsRequest) throws Exception {

		if (formElementsArray != null) {

			List<FileUploadRequest> fileUploadRequests = new ArrayList<>();

			for (int item = 0; item < formElementsArray.length(); item++) {

				JSONObject attribute = formElementsArray.optJSONObject(item);

				if (attribute != null) {

					String referenceId = attribute.optString("referenceId");
					String description = attribute.optString("description");

					FileUploadRequest fileUploadRequest = new FileUploadRequest();

					fileUploadRequest.setReferenceId(referenceId);
					fileUploadRequest.setCategory(description);
					fileUploadRequest.setFunctionality("module-file-up");
					fileUploadRequest.setExpiresInMinutes(30);

					fileUploadRequest.setMaxFileSize(attribute.optLong("maxFileSize"));

					fileUploadRequest.setMinFileSize(attribute.optLong("minFileSize"));

					fileUploadRequest.setMinWidth(attribute.optInt("minImageWidth"));

					fileUploadRequest.setMaxWidth(attribute.optInt("maxImageWidth"));

					fileUploadRequest.setMinHeight(attribute.optInt("minImageHeight"));

					fileUploadRequest.setMaxHeight(attribute.optInt("maxImageHeight"));

					fileUploadRequest.setAllowedMime(attribute.optJSONArray("allowedMimeTypes") != null ? attribute
							.optJSONArray("allowedMimeTypes").toList().stream().map(Object::toString).toList() : null);

					fileUploadRequests.add(fileUploadRequest);
				}
			}

			createUploadSessionsRequest.setFiles(fileUploadRequests);
		}
	}

}
