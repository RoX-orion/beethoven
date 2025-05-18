package org.beethoven.pojo.dto;

import lombok.Data;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-21
 */

@Data
public class PageDTO {

    private Integer page = 1;

    private Integer size = 10;
}
