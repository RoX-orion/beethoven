package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.beethoven.pojo.enums.UserType;
import org.jetbrains.annotations.NotNull;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-15
 */

@Data
public class OAuth2Login {

    @NotBlank
    private String code;

    @NotNull
    private UserType type;
}
