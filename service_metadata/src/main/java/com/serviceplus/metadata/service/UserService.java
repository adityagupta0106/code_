package com.serviceplus.metadata.service;

import com.serviceplus.metadata.dto.*;
import com.serviceplus.metadata.feignClient.LgdFeignClient;
import com.serviceplus.metadata.feignClient.UserManagementClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.serviceplus.metadata.utility.ApplicationConstants.ROLE_EXECUTIVE;
import static com.serviceplus.metadata.utility.ApplicationConstants.ROLE_WORKFLOW_OFFICER;
import static com.serviceplus.metadata.utility.ApplicationConstants.ROLE_DESIGNATED_OFFICER;
import static com.serviceplus.metadata.utility.ApplicationConstants.ROLE_DATA_ENTRY_OPPERATOR;
import static com.serviceplus.metadata.utility.CommonUtil.*;
import static java.util.Objects.requireNonNull;

@Service
public class UserService {

    @Value("${aesServiceKey}")
    private String AES_AUTH_KEY;

    private final UserManagementClient userManagementClient;
    
    private final LgdFeignClient lgdFeignClient;

    @Autowired
    public UserService(UserManagementClient userManagementClient, LgdFeignClient lgdFeignClient) {
        this.userManagementClient = userManagementClient;
		this.lgdFeignClient = lgdFeignClient;
    }

    //LATER USE A BATCH API
    public String getUserName(Long userId) {
        try {
            UserProfile profile = fetchUserProfile(userId);
            return profile != null ? profile.getData().getProfile().getLd_signNo().concat("(")
                    .concat(profile.getData().getProfile().getU_emailId()).concat(")") : "";
        } catch (Exception ex) {
            return "";
        }
    }

    public UserProfile fetchUserProfile(Long userId){
        String query = fetchProfileApiBody(requireNonNull(AESEncrypt(userId.toString(), AES_AUTH_KEY)),
                "u_userName  ul_entityId  ul_locationId u_designationId ul_entityLevelId ul_categoryId u_emailId ld_signNo");

        ResponseEntity<UserProfile> response = userManagementClient.getUserProfile(getServicePlusInternalRequestAuth(), query);
        return response.getBody();
    }

    public FetchRoleResponse fetchUserRoles(Long userId){
        FetchRoles role = new FetchRoles(requireNonNull(AESEncrypt(userId.toString(), AES_AUTH_KEY)));
        ResponseEntity<FetchRoleResponse> response = userManagementClient.getUserRoles(getServicePlusInternalRequestAuth(), entityToString(role));
        return response.getBody();
    }

    public boolean canAssignUserToTask(WorkflowAssignmentTokenRequest.TaskAssignmentNode.Users user,
                                       Integer serviceId,
                                       String locationId,
                                       String taskId) {


        FetchRoleResponse data = fetchUserRoles(user.getId());
        List<UserSessionDTO.Roles> roles = data.getData().getRoles();

        return roles.stream().anyMatch(r -> r.getRoleId() == ROLE_EXECUTIVE);
    }

    public void addWorkflowRole(Long userId) {

        UserRoleRequest request = new UserRoleRequest(userId, ROLE_WORKFLOW_OFFICER);
        userManagementClient.addRole(getServicePlusInternalRequestAuth(), request);
    }

    public void removeWorkflowRole(Long userId) {

        UserRoleRequest request = new UserRoleRequest(userId, ROLE_WORKFLOW_OFFICER);
        userManagementClient.removeRole(getServicePlusInternalRequestAuth(), request);
    }
    
    public void addDesignatedOfficerRole(Long userId) {
    	UserRoleRequest request = new UserRoleRequest(userId, ROLE_DESIGNATED_OFFICER);
    	userManagementClient.addRole(getServicePlusInternalRequestAuth(), request);
    }
    
    public void addDEORole(Long userId) {
    	UserRoleRequest request = new UserRoleRequest(userId, ROLE_DATA_ENTRY_OPPERATOR);
    	userManagementClient.addRole(getServicePlusInternalRequestAuth(), request);
    }
    
    public String getDesignationName(DesignationRequestDTO  requestDto) {
    	try {
            ResponseEntity<DesignationDTO> response =lgdFeignClient.getDesignationName(requestDto);
            if (response.getStatusCode().is2xxSuccessful()&& response.getBody() != null) {
                return response.getBody().getDesignationName();
            }
        } catch (Exception ex) {
            ex.printStackTrace();;
        }
        return "";
    }
    
}
