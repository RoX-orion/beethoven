package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-28
 */

@Data
public class AddPlaylistDto {

    @Size(min = 1, max = 32)
    @NotBlank(message = "歌单名称不能为空")
    private String title;

    private String introduction;
}
