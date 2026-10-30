package com.serviceplus.formDesigner.domain.cache;

import com.serviceplus.formDesigner.dto.FormProcessCacheDTO;

public interface CacheService {
	public void saveJson(FormProcessCacheDTO dtoObject,String key);

	public Object get(String key);

	public void delete(String key);

	boolean dataExists(String key);

    boolean add(Object entity, String key, boolean durationRequired, Integer minutes);

    <T> T fetch(String key, Class<T> clazz);
}
