package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-30
 */

@Data
public class StorageDTO {

    @NotNull
    private Integer id;

    @NotBlank
    private String provider;

    @NotBlank
    private String accessKey;

    @NotBlank
    private String secretKey;

    @NotBlank
    private String bucket;

    @NotBlank
    private String endpoint;
}
