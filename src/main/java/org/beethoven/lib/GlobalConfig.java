package org.beethoven.lib;

import org.springframework.context.annotation.Configuration;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-03
 */

@Configuration
public class GlobalConfig {

    public int shardingSize = Constant.DEFAULT_SHARDING_SIZE;


}
