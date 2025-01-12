package org.beethoven.pojo;

import lombok.Data;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-15
 */

@Data
public class OAuth2Info {

    private String clientId;

    private String redirectUri;

    private String state;
}
