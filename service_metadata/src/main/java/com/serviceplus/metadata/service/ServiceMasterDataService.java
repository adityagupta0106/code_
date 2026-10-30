package com.serviceplus.metadata.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceMasterData;
import com.serviceplus.metadata.repository.IServiceMasterDataRepository;

@Service
public class ServiceMasterDataService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceMasterDataService.class);

    @Autowired
    private IServiceMasterDataRepository masterDataRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Object getServiceCategory() {
        return getDataFromCacheOrDb("category",null);
    }

    public Object getServiceActions() {
        return getDataFromCacheOrDb("actions",null);
    }

    public Object getServiceType() {
        return getDataFromCacheOrDb("service type",null);
    }

    public Object getGoals() {
        return getDataFromCacheOrDb("development goal",null);
    }
    
	private Object getDataFromCacheOrDb(String name, UserSessionDTO userSessionDetails) {
		Object data = new ArrayList<>();
		try {
			String cacheKey = "service:" + name;
			if (name.equals("Charge Type") && userSessionDetails != null && userSessionDetails.getClcId() != null) {
				cacheKey = "service~stateCode:" + name + "~" + userSessionDetails.getClcId();
			}
			String cachedData = fetchFromRedisCache(cacheKey);
			boolean cacheExpired = false;
			
			if (cachedData == null || cacheExpired) {
				ServiceMasterData masterData = masterDataRepository.findByName(name)
						.orElseThrow(() -> new RuntimeException(name + " not found"));

				if (name.matches("Charge Type|Payment Mode")) {
					List<Object> filteredData = filterDataByStateCode(masterData.getData(), userSessionDetails);
					String json = objectMapper.writeValueAsString(filteredData);
					addToRedisCache(cacheKey.trim(), json);
					return filteredData;
				} else {
					addToRedisCache(cacheKey.trim(), masterData.getData());
					return parseJsonData(masterData.getData());
				}
			} else {
				return parseJsonData(cachedData);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return data;
	}

    private List<Object> filterDataByStateCode(String jsonData, UserSessionDTO userSessionDetails) {
        List<Object> filteredData = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> dataList = mapper.readValue(jsonData, new TypeReference<List<Map<String, Object>>>(){});
            
            Integer userStateId = userSessionDetails != null ? userSessionDetails.getClcId() : null;
            
            for (Map<String, Object> item : dataList) {
                Integer stateCode = (Integer) item.get("definedStateCode");
                if (stateCode == null || stateCode == 0 ||(userStateId != null && userStateId.equals(stateCode))) {
                    filteredData.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filteredData;
    }
    private String fetchFromRedisCache(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return cached.toString();
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("Error fetching from Redis for key {}: {}", key, e.getMessage());
        }
        return null;
    }

    private void addToRedisCache(String key, String data) {
        try {
            redisTemplate.opsForValue().set(key, data);
            logger.info("Cached data for key {}", key);
        } catch (Exception e) {
            logger.error("Error caching to Redis for key {}: {}", key, e.getMessage());
        }
    }

	private Object parseJsonData(String json) {

		try {
			JsonNode jsonNode = objectMapper.readTree(json);
			if (jsonNode.isArray()) {
				return objectMapper.convertValue(jsonNode, new TypeReference<List<Object>>() {
				});
			}
			if (jsonNode.isObject()) {
				return objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {
				});
			}
			return objectMapper.convertValue(jsonNode, Object.class);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON data: " + e.getMessage(), e);
		}
	}

    public Object getEnclosures() {
        return getDataFromCacheOrDb("Enclosure",null);
    }

    public Object getDocumentRecommended() {
        return getDataFromCacheOrDb("Document Data",null);
    }

	public Object getSubmissionMode() {
        return getDataFromCacheOrDb("Submission Mode",null);
    }

	public Object getChargeType(UserSessionDTO userSessionDetails) {
		return getDataFromCacheOrDb("Charge Type",userSessionDetails);
	}

	public Object getPaymentMode(UserSessionDTO userSessionDetails) {
		return getDataFromCacheOrDb("Payment Mode",userSessionDetails);
	}
	
	public Object getCatalogueQuestioner() {
        return getDataFromCacheOrDb("catalogue questions",null);
    }

	public Object getEsignMaster() {
		return getDataFromCacheOrDb("esign master",null);
	}

	public Object getBiometricTypes() {
		return getDataFromCacheOrDb("biometric type",null);
	}
	
}
