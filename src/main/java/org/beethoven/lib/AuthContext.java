package org.beethoven.lib;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-02-15
 */

@Component
public class AuthContext {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String getUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return stringRedisTemplate.opsForValue().get(Constant.PREFIX.USER_ID + token);
    }
}
