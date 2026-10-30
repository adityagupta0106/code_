package com.serviceplus.tracking.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serviceplus.tracking.dto.UserSessionDTO;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
	
	public static UserSessionDTO getUserSessionDetails(HttpServletRequest request) {
		String user = request.getHeader("USER-DETAILS");
		if(isEmpty(user))
			return null;
			
		return (UserSessionDTO) stringToEntity(user,UserSessionDTO.class);
	}
	
	public static Object stringToEntity(String data,Class<?> classs) {
		return new Gson().fromJson(data, classs);
	}


	public static boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
	
	public static String buildUserToken(Integer serviceId, String taskId, Integer locationId) {
		//logic?
	    return serviceId + "~" + taskId + "~" + locationId;
	}

    public static String entityToString(Object data) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create().toJson(data);
    }
    
    public static Map<String, Object> extractHeaders(HttpServletRequest request) {
		Map<String, Object> headers = new HashMap<>();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				headers.put(headerName, request.getHeader(headerName));
			}
		}
		return headers;
	}
    public static String extractDocumentTinyUrlKey(String documentTinyUrl) {

        if (documentTinyUrl == null
                || documentTinyUrl.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Document tiny URL is required");
        }

        String url = documentTinyUrl.trim();

        int index = url.indexOf("/dt/");

        if (index == -1) {
            throw new IllegalArgumentException(
                    "Invalid document tiny URL: " + documentTinyUrl);
        }

        String urlKey = url.substring(index + 4);

        if (urlKey.isEmpty()) {
            throw new IllegalArgumentException(
                    "Document tiny URL key is missing");
        }

        return urlKey;
    }
	
    public static String extractActionTinyUrlKey(String actionTinyUrl) {

        if (actionTinyUrl == null
                || actionTinyUrl.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Action tiny URL is required");
        }

        String url = actionTinyUrl.trim();

        int index = url.indexOf("/at/");

        if (index == -1) {
            throw new IllegalArgumentException(
                    "Invalid action tiny URL: " + actionTinyUrl);
        }

        String urlKey = url.substring(index + 4);

        if (urlKey.isEmpty()) {
            throw new IllegalArgumentException(
                    "Action tiny URL key is missing");
        }

        return urlKey;
    }
}
