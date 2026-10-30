package com.serviceplus.metadata.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.metadata.entity.ServiceLog;

@Repository
public interface IServiceLogRepository extends JpaRepository<ServiceLog, Object> {

	ServiceLog findByServiceId(Integer serviceId);

	List<ServiceLog> findByServiceIdInAndServiceStatus(List<Integer> serviceIds, Integer status);

	List<ServiceLog> findByServiceIdIn(List<Integer> serviceIds);
	
	ServiceLog findByServiceIdAndTenantIdAndServiceStatusIn(Integer serviceId, String tenantId, List<Integer> status);

	List<ServiceLog> findByServiceIdInAndServiceStatusAndTenantId(List<Integer> serviceIds, Integer status,String tenantId);

	List<ServiceLog> findByServiceIdInAndTenantId(List<Integer> serviceIds, String tenantId);

	List<ServiceLog> findByServiceStatus(Integer status);
	
	List<ServiceLog> findByServiceStatusInAndTenantId(List<Integer> statuses, String tenantId);

	@Modifying
	@Transactional
	@Query("""
	    update ServiceLog s
	       set s.serviceStatus = 4,
	           s.launchFlag = 'Y',
	           s.launchApprovedBy = :userId,
	           s.launchedDate = :currentDate,
	           s.uDate = :currentDate
	     where s.tenantId=:tenantId and s.serviceId = :serviceId
	       and s.serviceStatus = 3
	    """)
	int moveToProduction(@Param("serviceId") Integer serviceId,
	                     @Param("userId") Long userId,
	                     @Param("currentDate") Date currentDate,@Param("tenantId") String tenantId);
	@Query("""
		    SELECT s.serviceId
		    FROM ServiceLog s
		    WHERE s.serviceStatus NOT IN :statuses
		      AND s.tenantId = :tenantId
		""")
	List<Integer> findServiceIdByServiceStatusNotInAndTenantId(
	        @Param("statuses") List<Integer> statuses,
	        @Param("tenantId") String tenantId);

	Optional<ServiceLog> findByServiceIdAndServiceStatusNotInAndTenantId(Integer serviceId, List<Integer> statusList,
			String tenantId);

	Optional<ServiceLog> findByServiceIdAndServiceStatusAndTenantId(Integer serviceId, int code, String tenantId);

	@Modifying
	@Transactional
	void deleteByServiceId(Integer serviceId);
}
