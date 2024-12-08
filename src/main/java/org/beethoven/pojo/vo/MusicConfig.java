package org.beethoven.pojo.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-07
 */

@Data
public class MusicConfig {

    private MultipartFile defaultMusicCoverFile;

    private String defaultMusicCover;

    private int shardingSize;
}
