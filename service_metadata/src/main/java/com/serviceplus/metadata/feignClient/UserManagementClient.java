package com.serviceplus.metadata.feignClient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.serviceplus.metadata.dto.FetchRoleResponse;
import com.serviceplus.metadata.dto.UserProfile;
import com.serviceplus.metadata.dto.UserRoleRequest;

@FeignClient(name = "USERMANAGEMENT",path = "/usermgmt")
public interface UserManagementClient {

    @PostMapping(value = "/g/gapi",consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserProfile> getUserProfile(@RequestHeader Map<String,String> headers, @RequestBody String bodyContent);
    @PostMapping(value = "/a/roles",consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FetchRoleResponse> getUserRoles(@RequestHeader Map<String,String> headers, @RequestBody String bodyContent);

    @PostMapping(value = "/b/user-role/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> addRole(@RequestHeader Map<String, String> headers, @RequestBody UserRoleRequest request);

    @PostMapping(value = "/b/user-role/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> removeRole(@RequestHeader Map<String, String> headers, @RequestBody UserRoleRequest request);
    
}
