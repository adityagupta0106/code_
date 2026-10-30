package com.serviceplus.metadata.mvel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.mvel.entity.MvelFunction;

@Repository
public interface IMvelFunctionRepository extends JpaRepository<MvelFunction, Integer> {
	public MvelFunction findByServiceIdAndName(Integer serviceId,String name);
	public List<MvelFunction> findByServiceId(Integer serviceId);
	public MvelFunction findByIdAndServiceIdAndCreatedBy(Long id,Integer serviceId,Long createdBy);
	public MvelFunction findById(Long id);
	public MvelFunction findByIdAndServiceId(Long id,Integer serviceId);
	public void deleteByServiceId(Integer serviceId);

}
