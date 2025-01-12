package org.beethoven.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-01-12
 */

public class RequestUtil {

    private RequestUtil(){}

    public static String getToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
