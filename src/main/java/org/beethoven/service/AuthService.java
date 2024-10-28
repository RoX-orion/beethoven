package org.beethoven.service;

import org.springframework.stereotype.Service;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-28
 */

@Service
public class AuthService {

    private final Integer USER_ID = 1;

    public Integer getUserId() {
        return USER_ID;
    }
}
