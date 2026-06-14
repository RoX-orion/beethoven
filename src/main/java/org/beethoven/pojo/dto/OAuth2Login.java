package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.beethoven.pojo.enums.UserType;

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
