package org.beethoven.pojo.vo;

import lombok.Data;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-01
 */

@Data
public class AppConfig {

    private String defaultMusicCover;

    private String brand;

    private int shardingSize;
}
