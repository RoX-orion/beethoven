package org.beethoven.lib.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.entity.ApiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-02-15
 */

@Slf4j
@Component
public class PermissionHandler implements HandlerInterceptor {

    @Resource
    private ObjectMapper mapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Comma-separated account IDs that are allowed to perform administrator-only operations.
     * An empty value intentionally denies all administrator-only operations.
     */
    @Value("${security.admin-user-ids:}")
    private String adminUserIds;

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
            if (permission != null) {
                String token = request.getHeader("Authorization");
                if (!StringUtils.hasText(token)) {
                    write(response, ApiResult.expired("UNAUTHORIZED"), HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                String userId = stringRedisTemplate.opsForValue().get(Constant.PREFIX.USER_ID + token);
                if (!StringUtils.hasText(userId)) {
                    write(response, ApiResult.expired("UNAUTHORIZED"), HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }

                if ("ADMIN".equalsIgnoreCase(permission.value()) && !isAdministrator(userId)) {
                    write(response, ApiResult.fail(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN"), HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAdministrator(String userId) {
        Set<String> configuredAdminIds = Arrays.stream(adminUserIds.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        return configuredAdminIds.contains(userId);
    }

    private void write(HttpServletResponse response, ApiResult<?> apiResult, int status) {
        try {
            response.setStatus(status);
            response.setContentType("application/json;charset=UTF-8");
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.println(mapper.writeValueAsString(apiResult));
        } catch (IOException e) {
            log.error("write response error", e);
        }
    }
}
