package com.serviceplus.tracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.serviceplus.tracking.dto.ServiceDropdownDTO;
import com.serviceplus.tracking.dto.TaskDropdownDTO;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.entity.TaskMaster;
import com.serviceplus.tracking.entity.UserSentBox;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.serviceplus.tracking.utility.CommonUtil.getUserSessionDetails;

@Service
public class SentBoxFilterService {

    @PersistenceContext
    private EntityManager entityManager;

    private final WorkflowInboxRedisService workflowInboxRedisService;

    @Autowired
    public SentBoxFilterService(WorkflowInboxRedisService workflowInboxRedisService) {
        this.workflowInboxRedisService = workflowInboxRedisService;
    }

    public List<ServiceDropdownDTO> getServices(HttpServletRequest request) {

        UserSessionDTO session = getUserSessionDetails(request);

        Set<String> holderIdStrings = workflowInboxRedisService
                                    .getTokensByUserAndLocation(
                                            session.getUserID(),
                                            session.getLocationId().toString())
                                    .stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.toSet());

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<UserSentBox> root = query.from(UserSentBox.class);

        query.multiselect(root.get("baseServiceId"), root.get("serviceName"));
        query.distinct(true);
        query.where(cb.and(cb.equal(root.get("tenantId"), session.getTenantId()),root.get("holderId").in(holderIdStrings)));
        query.orderBy(cb.asc(root.get("serviceName")));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        return tuples.stream().map(tuple -> {

            ServiceDropdownDTO dto = new ServiceDropdownDTO();
            dto.setBaseServiceId(tuple.get(0, Integer.class));
            dto.setServiceName(tuple.get(1, String.class));

            return dto;

        }).toList();
    }

	public List<TaskDropdownDTO> getTasks(Integer baseServiceId, HttpServletRequest request) {

		UserSessionDTO session = getUserSessionDetails(request);

		Set<String> holderIdStrings = workflowInboxRedisService
				.getTokensByUserAndLocation(session.getUserID(), session.getLocationId().toString()).stream()
				.map(String::valueOf).collect(Collectors.toSet());

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Tuple> query = cb.createTupleQuery();

		Root<UserSentBox> root = query.from(UserSentBox.class);

		Root<TaskMaster> taskMaster = query.from(TaskMaster.class);

		query.multiselect(root.get("taskId").alias("taskId"), root.get("taskName").alias("taskName"),
				taskMaster.get("taskProperty").alias("taskProperty"));

		query.distinct(true);

		query.where(cb.and(cb.equal(root.get("tenantId"), session.getTenantId()),
				cb.equal(root.get("baseServiceId"), baseServiceId), root.get("holderId").in(holderIdStrings),

				cb.equal(root.get("taskId"), taskMaster.get("taskId")),
				cb.equal(root.get("tenantId"), taskMaster.get("tenantId")),
				cb.equal(root.get("baseServiceId"), taskMaster.get("baseServiceId"))));

		query.orderBy(cb.asc(root.get("taskName")));

		List<Tuple> tuples = entityManager.createQuery(query).getResultList();

		return tuples.stream().map(tuple -> {

			TaskDropdownDTO dto = new TaskDropdownDTO();
			dto.setTaskId(tuple.get("taskId", String.class));
			dto.setTaskName(tuple.get("taskName", String.class));
			dto.setTaskProperty(tuple.get("taskProperty", JsonNode.class));

			return dto;

		}).toList();
	}
}