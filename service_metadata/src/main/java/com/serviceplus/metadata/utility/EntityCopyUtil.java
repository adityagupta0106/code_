package com.serviceplus.metadata.utility;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Constructor;
import java.util.Map;

public final class EntityCopyUtil {

    private EntityCopyUtil() {
    }

    public static <T> T copy(Object source, Class<T> targetClass, String... ignoredProperties) {

        if (source == null) {
            return null;
        }

        try {
            Constructor<T> constructor = targetClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            T target = constructor.newInstance();

            BeanUtils.copyProperties(source, target, ignoredProperties);

            return target;

        } catch (Exception e) {

            throw new IllegalStateException("Unable to copy " + source.getClass().getName() + " to " + targetClass.getName(), e);
        }
    }

    public static <T> T copy(Object source, Class<T> targetClass, Map<String, Object> overrides, String... ignoredProperties) {

        T target = copy(source, targetClass, ignoredProperties);

        setProperties(target, overrides);

        return target;
    }

    public static void setProperties(Object target, Map<String, Object> properties) {

        if (target == null || properties == null || properties.isEmpty()) {
            return;
        }

        BeanWrapper wrapper = new BeanWrapperImpl(target);

        properties.forEach((property, value) -> {

            if (wrapper.isWritableProperty(property)) {
                wrapper.setPropertyValue(property, value);
            }
        });
    }

    public static void setIfPresent(Object target, String property, Object value) {

        if (target == null) {
            return;
        }

        BeanWrapper wrapper = new BeanWrapperImpl(target);

        if (wrapper.isWritableProperty(property)) {
            wrapper.setPropertyValue(property, value);
        }
    }

    public static Object getIfPresent(Object target, String property) {
        if (target == null) {
            return null;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        if (wrapper.isReadableProperty(property)) {
            return wrapper.getPropertyValue(property);
        }
        return null;
    }

    public static boolean hasProperty(Object target, String property) {
        if (target == null) {
            return false;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        return wrapper.isReadableProperty(property) && wrapper.isWritableProperty(property);
    }
}