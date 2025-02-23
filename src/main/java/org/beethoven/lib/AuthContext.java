package org.beethoven.lib;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

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

    public Long getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization");
        String userId = stringRedisTemplate.opsForValue().get(Constant.PREFIX.USER_ID + token);
        if (StringUtils.hasText(userId)) {
            return Long.valueOf(userId);
        }

        throw new RuntimeException();
    }
}
