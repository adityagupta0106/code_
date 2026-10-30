package com.serviceplus.metadata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinition;

@Repository
public interface IServiceDeliveryUnitDefinitionRepository extends JpaRepository<ServiceDeliveryUnitDefinition,Object>{

	ServiceDeliveryUnitDefinition findByServiceId(Integer serviceId);

	ServiceDeliveryUnitDefinition findByServiceIdAndUserId(Integer serviceId, Long longValue);

	void deleteByServiceId(Integer serviceId);

}
