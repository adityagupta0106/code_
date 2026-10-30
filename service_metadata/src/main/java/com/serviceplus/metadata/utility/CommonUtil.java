package com.serviceplus.metadata.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;
import com.serviceplus.metadata.dto.OfficeDetailsDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceMetadata;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;

import static com.serviceplus.metadata.utility.ApplicationConstants.STATIC_AUTH_TOKEN;
import static com.serviceplus.metadata.utility.SnowflakeIdGenerator.createUniqueId;

public class CommonUtil {
	
	public static UserSessionDTO getUserSessionDetails(HttpServletRequest request) {
		String user = request.getHeader("USER-DETAILS");
		if(isEmpty(user))
			return null;
			
		return (UserSessionDTO) stringToEntity(user,UserSessionDTO.class);
	}
	
	private static UserSessionDTO getLocalSessionDetails() {
		 UserSessionDTO userSessionDetails=new UserSessionDTO();
	    	userSessionDetails.setUserID(-2L);
//	    	userSessionDetails.setStateId(32);
//	    	userSessionDetails.setDepartmentId(544);
//	    	userSessionDetails.setDepartmentLevelId(852);
	    	userSessionDetails.setEntityId(4);
	    	userSessionDetails.setLocationName("Localtion clc name");
	    	userSessionDetails.setUserName("User Name D");
	    	userSessionDetails.setLocationId(12);
	    	userSessionDetails.setTenantId("69e5c292a2b521ec6ca9cdd7");
			return userSessionDetails;
		}  
	public static Object stringToEntity(String data,Class<?> classs) {
		return new Gson().fromJson(data, classs);
	}


	public static boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
	
	public static String generateChecksums(String serviceId, int numIterations) throws Exception {
        String[] checksums = new String[numIterations];
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < numIterations; i++) {
            byte[] randomBytes = new byte[32];
            random.nextBytes(randomBytes);
            String randomHex = DatatypeConverter.printHexBinary(randomBytes).toLowerCase();
            String combined = serviceId + randomHex;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes("UTF-8"));
            checksums[i] = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
        }
        
        return checksums[numIterations-1];
    }
	
	public static Cookie createCookie(String name,String Value,boolean httpflag,boolean secureflag,String sameSite,String path) {
		Cookie cookie = new Cookie(name, Value); 
		cookie.setSecure(secureflag);
		cookie.setAttribute("SameSite", sameSite); 
		cookie.setHttpOnly(httpflag);		
		cookie.setPath(path);
		return cookie;
	}
	public static String AESEncrypt(String content, String authKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(authKey.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec,new IvParameterSpec(new byte[16]));
			byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
			String finalString = org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);
			
			return finalString;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String AESDecrypt(String content, String authKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(authKey.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec,new IvParameterSpec(new byte[16]));
			byte[] original = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(content));

			return new String(original);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createAttributeId(String taskId,String formId,String attrId) {
		return taskId + "$" + formId + "$" + attrId;
	}
		
	public static String getRandomAlfaNumeric(int length) {
		final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890";
		StringBuilder result = new StringBuilder();
		while (length > 0) {
			Random rand = new Random();
			result.append(characters.charAt(rand.nextInt(characters.length())));
			length--;
		}
		return result.toString();
	}
	
	public static ResponseEntity<?> validateServiceToken(HttpServletRequest request, Integer serviceId, JSONObject responseJson) {
//      Cookie[] cookieArr = request.getCookies();
//      Cookie serviceToken = null;
//
//      if (cookieArr != null) {
//          for (Cookie cookie : cookieArr) {
//              if ("serviceToken".equals(cookie.getName())) {
//                  serviceToken = cookie;
//                  break;
//              }
//          }
//      }
//
//      if (serviceToken != null && serviceToken.getValue() != null) {
//          try {
//              String token = CommonUtil.AESDecrypt(serviceToken.getValue(), aesServiceKey);
//              if (!serviceRequestService.isChecksumValid(token, serviceId)) {
//                  responseJson.put("errorCode", "403");
//                  responseJson.put("errorMessage", "Forbidden Service");
//                  return new ResponseEntity<>(responseJson.toString(), HttpStatus.FORBIDDEN);
//              }
//          } catch (Exception e) {
//              responseJson.put("errorCode", "500");
//              responseJson.put("errorMessage", "Token Decryption Failed: " + e.getMessage());
//              return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
//          }
//      } else {
//          responseJson.put("errorCode", "400");
//          responseJson.put("errorMessage", "Invalid Service");
//          return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
//      }
      return null; 
  }

    public static String generateUniqueToken(String PREFIX){
            return PREFIX.concat(createUniqueId());
        }

    public static Map<String,String> getServicePlusInternalRequestAuth(){
        Map<String,String> headers = new HashMap<>();
        headers.put("_scall", "true");
        headers.put("Atk", STATIC_AUTH_TOKEN);
        return headers;
    }

    public static String fetchProfileApiBody(String userId,String params) {
        String body = "query { profile(id: \\\"".concat(userId.toString()).concat("\\\") { ").concat(params).concat(" }}");
        return "{\"query\":\"".concat(body).concat("\"}");
    }

    public static String entityToString(Object data) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create().toJson(data);
    }

    public static Integer serviceIdToBaseServiceId(Integer serviceId){
        return serviceId / 10000;
    }
    
    public static String getTaskName(ServiceMetadata metadata, String taskId) {

        return metadata
                .getMetadataJson()
                .getOfficeDetails()
                .stream()
                .filter(x ->
                        taskId.equals(
                                x.getTaskId()))
                .map(OfficeDetailsDTO::getTaskId)
                .findFirst()
                .orElse("");
    }

    public static String getLocationName(
            ServiceMetadata metadata,
            String taskId,
            String locationId) {

        return metadata
                .getMetadataJson()
                .getOfficeDetails()
                .stream()
                .filter(x ->
                        taskId.equals(
                                x.getTaskId()))
                .flatMap(x ->
                        x.getAllowedOffices()
                                .stream())
                .filter(x ->
                        locationId.equals(
                                String.valueOf(
                                        x.getOrgUnitCode())))
                .map(OfficeDetailsDTO.OfficeUnitData::getOrgUnitName)
                .findFirst()
                .orElse("");
    }
    public static String fileToBase64(String path) {
		File file = new File(path);
		
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            return Base64.getEncoder().encodeToString(fileBytes);
            
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
	}
    
}
