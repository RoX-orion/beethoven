package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;
}
