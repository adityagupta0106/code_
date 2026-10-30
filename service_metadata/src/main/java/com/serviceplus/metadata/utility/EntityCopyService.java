package com.serviceplus.metadata.utility;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class EntityCopyService {

    public <T> T copy(
            Object source,
            Class<T> targetClass,
            String... ignoredProperties) {

        try {

            T target =
                    targetClass.getDeclaredConstructor()
                            .newInstance();

            BeanUtils.copyProperties(
                    source,
                    target,
                    ignoredProperties
            );

            return target;

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to copy entity from "
                            + source.getClass().getSimpleName()
                            + " to "
                            + targetClass.getSimpleName(),
                    e
            );
        }
    }
}
