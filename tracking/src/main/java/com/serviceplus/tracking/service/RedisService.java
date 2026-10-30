package com.serviceplus.tracking.service;

import com.serviceplus.tracking.service.interfaces.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RedisService implements ICacheService {

    private static final Logger logger =
            LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void saveValue(String key,Object value) {

        logger.info("Saving redis value for key : {}",key);

        redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public Object getValue(String key) {

        logger.info("Fetching redis value for key : {}",key);

        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteKey(String key) {

        logger.info("Deleting redis key : {}",key);

        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {

        return redisTemplate.hasKey(key);
    }

    @Override
    public void addToSet(String key,Object value) {

        logger.info("Adding value in redis set for key : {}",key);

        redisTemplate.opsForSet().add(key,value);
    }

    @Override
    public Set<Object> getSetMembers(String key) {

        logger.info("Fetching set members for key : {}",key);

        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public void removeFromSet(String key,Object value) {

        logger.info("Removing value from redis set for key : {}",key);

        redisTemplate.opsForSet().remove(key,value);
    }

    @Override
    public Long getSetSize(String key) {

        return redisTemplate.opsForSet().size(key);
    }
}
