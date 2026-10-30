package com.serviceplus.formDesigner.returnToEdit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.returnToEdit.entity.FormDataReference;
import com.serviceplus.formDesigner.returnToEdit.entity.FormDataReference.FormDataReferenceId;

@Repository
public interface FormDataReferenceRepository extends JpaRepository<FormDataReference, FormDataReferenceId> {
	List<FormDataReference> findByServiceIdAndApplicationId(Integer serviceId, String applicationId);

	@Query(value = """
			SELECT *
			FROM schm_sp.form_data_reference
			WHERE service_id = :serviceId
			  AND application_id = :applicationId
			ORDER BY cr_date DESC NULLS LAST
			LIMIT 1
			""", nativeQuery = true)
	Optional<FormDataReference> findLatestReference(@Param("serviceId") Integer serviceId,
			@Param("applicationId") String applicationId);
}
