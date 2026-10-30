package com.serviceplus.formDesigner.infrastructure;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.domain.cache.CacheService;
import com.serviceplus.formDesigner.dto.FormProcessCacheDTO;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.util.ApplicationConstants;

@Service
public class RedisCacheService implements CacheService{


	private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    public RedisCacheService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    private final String KEY_PREFIX = "formId:";
	
	@Override
	public void saveJson(FormProcessCacheDTO dtoObject,String key) {
		try {
			String finalKey = KEY_PREFIX + key;
			String json = objectMapper.writeValueAsString(dtoObject);
			redisTemplate.opsForValue().set(finalKey, json,ApplicationConstants.FORM_DATA_TTL_DAYS, TimeUnit.DAYS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Object get(String key) {
		try {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + key);
        return json != null ? objectMapper.readValue(json, FormProcessCacheDTO.class) : null;
		}catch(Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	@Override
	public void delete(String key) {
		 redisTemplate.delete(KEY_PREFIX + key);		
	}
	
	@Override
	 public boolean dataExists(String key) {
	        return redisTemplate.hasKey(KEY_PREFIX + key);
     }

    @Override
    public boolean add(Object entity, String key, boolean durationRequired, Integer timeout) {

        try {

            String json = objectMapper.writeValueAsString(entity);

            if (durationRequired) {
                redisTemplate.opsForValue().set(key, json, timeout, TimeUnit.SECONDS);

            } else {
                redisTemplate.opsForValue().set(key, json);
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public <T> T fetch(String key, Class<T> clazz) {

        try {
            String json = redisTemplate.opsForValue().get(key);

            if (json == null || json.isBlank()) {
                return null;
            }

            return objectMapper.readValue(json, clazz);

        } catch (Exception e) {
            return null;
        }
    }
}
