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
import java.util.List;

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
            Class<?> clazz = result.getClass();
            if (result instanceof List<?> list) {
                for (Object item : list) {
                    Class<?> itemClazz = item.getClass();
                    Field[] fields = itemClazz.getDeclaredFields();
                    setValue(fields, item);
                }
            } else {
                Field[] fields = clazz.getDeclaredFields();
                setValue(fields, result);
            }
        }
    }

    public void setValue(Field[] fields, Object result) throws IllegalAccessException {
        for (Field field : fields) {
            SplicingValue splicingValueAnnotation = field.getAnnotation(SplicingValue.class);
            if (splicingValueAnnotation == null) {
                continue;
            }
            field.setAccessible(true);
            if (splicingValueAnnotation.value().equals(Constant.ENDPOINT_PREFIX)) {
                String value = (String) field.get(result);
                if (!StringUtils.hasText(value)) {
                    continue;
                }
                String url = storageContext.getURL(value);
                field.set(result, url);
            }
        }
    }
}
