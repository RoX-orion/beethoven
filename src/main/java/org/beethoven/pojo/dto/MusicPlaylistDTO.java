package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-25
 */

@Data
public class MusicPlaylistDTO {

    @NotBlank(message = "音乐不能为空!")
    private Long musicId;

    @NotBlank(message = "歌单不能为空!")
    private Long playlistId;
}
