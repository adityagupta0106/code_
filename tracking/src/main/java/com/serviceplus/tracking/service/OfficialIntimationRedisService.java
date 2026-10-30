package com.serviceplus.tracking.service;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.dto.OfficeDetailsDTO;
import com.serviceplus.tracking.dto.OfficialIntimationKafkaDTO;
import com.serviceplus.tracking.service.interfaces.ICacheService;

@Service
public class OfficialIntimationRedisService {

	private static final Logger log = LogManager.getLogger("messageBoxLogger");
	@Autowired
	private ICacheService redisService;
	
	public void addTokenForUsers(OfficialIntimationKafkaDTO event) {
		if (event.getAllowedOffices() == null) {
			return;
		}
		for (OfficeDetailsDTO.OfficeUnitData office : event.getAllowedOffices()) {
			if (office.getHolderIds() == null) {
				continue;
			}
			for (String holderId : office.getHolderIds()) {

				String inboxKey = buildInboxKey(holderId, office.getOrgUnitCode());

				redisService.addToSet(inboxKey, holderId);

				String lookupKey = buildLookupKey(event.getServiceId(), event.getAssociatedTaskId(), holderId,
						office.getOrgUnitCode());

				redisService.addToSet(lookupKey, holderId);

				log.info("Official Intimation Redis Mapping Added. holderId={}, associatedTask={}", holderId,
						event.getAssociatedTaskId());
			}
		}
	}

	public void removeTokenForUsers(OfficialIntimationKafkaDTO event) {
		if (event.getAllowedOffices() == null) {
			return;
		}
		for (OfficeDetailsDTO.OfficeUnitData office : event.getAllowedOffices()) {
			if (office.getHolderIds() == null) {
				continue;
			}
			for (String holderId : office.getHolderIds()) {
				String inboxKey = buildInboxKey(holderId, office.getOrgUnitCode());
				redisService.removeFromSet(inboxKey, holderId);
				if (redisService.getSetSize(inboxKey) == 0) {
					redisService.deleteKey(inboxKey);
				}
				String lookupKey = buildLookupKey(event.getServiceId(), event.getAssociatedTaskId(), holderId,
						office.getOrgUnitCode());
				redisService.removeFromSet(lookupKey, holderId);
				if (redisService.getSetSize(lookupKey) == 0) {
					redisService.deleteKey(lookupKey);
				}
			}
		}
	}
	public Set<Object> getTokensByUserAndLocation(String holderId, Integer locationId) {
		return redisService.getSetMembers(buildInboxKey(holderId, locationId));
	}
	public Set<Object> getTokenByServiceAssociatedTask(Integer serviceId, String associatedTaskId, String holderId,
			Integer locationId) {
		return redisService.getSetMembers(buildLookupKey(serviceId, associatedTaskId, holderId, locationId));
	}
	private String buildInboxKey(String holderId, Integer locationId) {
		return ("msg:user:" + holderId + ":loc:" + locationId).trim();
	}
	private String buildLookupKey(Integer serviceId, String associatedTaskId, String holderId, Integer locationId) {
		return ("msg:srv:" + serviceId + ":at:" + associatedTaskId + ":user:" + holderId + ":loc:" + locationId).trim();
	}
}
