package com.serviceplus.metadata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ServiceProcessFlow;

@Repository
public interface IProcessFlowRepository extends JpaRepository<ServiceProcessFlow,Object>{

	ServiceProcessFlow findByServiceIdAndUserId(Integer serviceId, Long userId);
	public ServiceProcessFlow findByServiceId(Integer serviceId);
	
	@Query(value = """
	        SELECT * FROM schm_sp.service_process_flow s
	        WHERE jsonb_path_exists(
	            s.process_flow_json,
	            '$.nodes[*] ? (@.data.formDetail.holderId == $holderId && @.data.formDetail.isSubscribed == true)',
	            jsonb_build_object('holderId', CAST(:holderId AS text))
	        )
	        """, nativeQuery = true)
	    List<ServiceProcessFlow> findActiveFlowsSubscribedToForm(@Param("holderId") String holderId);

		@Query(value = """
				SELECT node -> 'data' ->> 'name'
				FROM schm_sp.service_process_flow spf
				CROSS JOIN LATERAL jsonb_array_elements(
				    spf.process_flow_json -> 'nodes'
				) AS node
				WHERE spf.service_id = :serviceId
				AND (node -> 'data' -> 'payment' ->> 'chargeId')::bigint = :chargeId
				""", nativeQuery = true)
		List<String> findTasksUsingChargeDetail(@Param("serviceId") Integer serviceId,
				@Param("chargeId") Long chargeId);

	List<ServiceProcessFlow> findAllByServiceId(Integer serviceId);

	void deleteByServiceId(Integer serviceId);
}
