package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-28
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchDTO extends PageDTO {

    @NotBlank
    private String key;
}
