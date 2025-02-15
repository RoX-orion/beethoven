package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "音乐不能为空!")
    private Long musicId;

    @NotEmpty(message = "歌单不能为空!")
    private Long[] playlistIds;
}
