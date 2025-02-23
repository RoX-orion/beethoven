package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-21
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PlaylistDTO extends PageDTO {

    private Long id;

    @Size(min = 1, max = 32)
    @NotBlank(message = "歌单名称不能为空")
    private String title;

    private String introduction;

    private String cover;

    @NotNull
    private Boolean accessible;
}
