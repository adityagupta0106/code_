package com.serviceplus.tracking.repository;

import com.serviceplus.tracking.entity.UserSentBox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

@Repository
public interface UserSentBoxRepository extends JpaRepository<UserSentBox, Long>, JpaSpecificationExecutor<UserSentBox> {

    Page<UserSentBox> findByHolderIdInAndTenantId(Set<String> holderIds, String tenantId, Pageable pageable);

    List<UserSentBox> findByTenantIdOrderByServiceNameAsc(String tenantId);

    List<UserSentBox> findByBaseServiceIdAndTenantIdOrderByTaskNameAsc(Integer baseServiceId, String tenantId);
    
    
    @Modifying
    @Query("""
        update UserSentBox s
           set s.calledback = true,
               s.actionCode = :actionCode
         where s.processId = :processId
           and s.tenantId = :tenantId
    """)
    int markAsCalledBack(
            @Param("processId") String processId,
            @Param("tenantId") String tenantId,
            @Param("actionCode") Integer actionCode
    );
    
}


