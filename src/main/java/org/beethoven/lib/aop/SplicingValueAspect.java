package org.beethoven.lib.aop;

import jakarta.annotation.Resource;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;
import org.beethoven.lib.store.StorageContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-20
 */

@Aspect
@Component
public class SplicingValueAspect {

    @Resource
    private StorageContext storageContext;

    @AfterReturning(value = "execution(* org.beethoven.mapper..*(..))", returning = "result")
    public void splicingValue(Object result) throws IllegalAccessException {
        if (result != null) {
            Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
            setValue(result, visited);
        }
    }

    public void setValue(Object result, Set<Object> visited) throws IllegalAccessException {
        if (result == null || visited.contains(result)) {
            return;
        }
        visited.add(result);

        if (result instanceof List<?> list) {
            for (Object item : list) {
                setValue(item, visited);
            }
            return;
        }
        if (isSimpleValue(result.getClass())) {
            return;
        }

        Field[] fields = result.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            SplicingValue splicingValueAnnotation = field.getAnnotation(SplicingValue.class);
            if (splicingValueAnnotation != null && splicingValueAnnotation.value().equals(Constant.ENDPOINT_PREFIX)) {
                String value = (String) field.get(result);
                if (!StringUtils.hasText(value)) {
                    continue;
                }
                if (value.startsWith("http://") || value.startsWith("https://")) {
                    continue;
                }
                String url = storageContext.getURL(value);
                field.set(result, url);
                continue;
            }
            setValue(field.get(result), visited);
        }
    }

    private boolean isSimpleValue(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz.isEnum()
                || clazz.getName().startsWith("java.")
                || clazz.getName().startsWith("jakarta.")
                || clazz.getName().startsWith("org.springframework.");
    }
}
