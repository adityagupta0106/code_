package com.serviceplus.tracking.service.interfaces;

import java.util.Set;

public interface ICacheService {

    void saveValue(String key,Object value);

    Object getValue(String key);

    void deleteKey(String key);

    boolean hasKey(String key);

    void addToSet(String key,Object value);

    Set<Object> getSetMembers(String key);

    void removeFromSet(String key,Object value);

    Long getSetSize(String key);

}
