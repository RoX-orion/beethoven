package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-21
 */

@Data
public class AlbumDTO {

    @NotBlank(message = "专辑名不能为空")
    private String name;
}
