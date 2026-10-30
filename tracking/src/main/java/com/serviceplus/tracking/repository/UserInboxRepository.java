package com.serviceplus.tracking.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.entity.UserInbox;
@Repository
public interface UserInboxRepository extends JpaRepository<UserInbox, Object> {

	List<UserInbox> findByUserToken(String userToken);

	List<UserInbox> findByUserTokenIn(List<String> tokenKeys);

	List<UserInbox> findByUserTokenInAndServiceId(List<String> tokenKeys, Integer serviceId);

	List<UserInbox> findByUserTokenInAndBaseServiceId(List<String> tokenKeys, Integer baseServiceId);

	Page<UserInbox> findByUserTokenInAndServiceIdAndTaskIdAndApplRefNoContainingIgnoreCase(List<String> tokenKeys,
			Integer serviceId, String taskId, String trim, Pageable pageable);
	
	Page<UserInbox> findByUserTokenInAndBaseServiceIdAndTaskIdAndApplRefNoContainingIgnoreCase(List<String> tokenKeys,
			Integer serviceId, String taskId, String trim, Pageable pageable);

	Page<UserInbox> findByUserTokenInAndServiceIdAndTaskId(List<String> tokenKeys, Integer serviceId, String taskId,
			Pageable pageable);
	Page<UserInbox> findByUserTokenInAndBaseServiceIdAndTaskId(List<String> tokenKeys, Integer baseServiceId, String taskId,
			Pageable pageable);
    Page<UserInbox> findByUserTokenInOrderByIsPriorityDescLastActionOnDesc(List<String> userTokens, Pageable pageable);

    void deleteByCurrentProcessIdAndTenantId(String processId, String tenantId);

    Optional<UserInbox> findByCurrentProcessIdAndTenantId(String processId, String tenantId);

    List<UserInbox> findByCurrentProcessIdAndUserTokenInAndTenantId(String processId, Set<String> holderIdStrings, String tenantId);

    @Modifying
    @Query("""
        delete from UserInbox u
        where u.applId = :applicationId
          and u.tenantId = :tenantId 
          and u.baseServiceId =:baseServiceId
    """)
    void deleteByApplIdAndTenantIdAndBaseServiceId(@Param("applicationId") String applicationId,
                                          @Param("tenantId") String tenantId,@Param("baseServiceId") Integer baseServiceId);

	Page<UserInbox> findByUserTokenInAndApplIdInOrderByIsPriorityDescLastActionOnDesc(List<String> tokenKeys, List<String> filteredApplicationIds, Pageable pageable);

	List<UserInbox> findByUserTokenInAndApplId(List<String> tokenKeys, String applicationId);
}
