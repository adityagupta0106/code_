package com.serviceplus.metadata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.serviceplus.metadata.entity.SpeSequenceLastNo;
import com.serviceplus.metadata.utility.ApplicationConstants;

public interface ISpeSequenceLastNoRepository extends JpaRepository<SpeSequenceLastNo, Object>{
	
	@Query(value = "SELECT " + ApplicationConstants.SP_SCHEMA_NAME + ".fnc_generate_seq(:sequenceId)", nativeQuery = true)
	SpeSequenceLastNo getSequenceLastNo(@Param("sequenceId") Integer sequenceId);

}
