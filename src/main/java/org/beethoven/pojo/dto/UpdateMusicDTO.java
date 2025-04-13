package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-04-13
 */

@Data
public class UpdateMusicDTO {

    @NotNull
    private Long musicId;

    private MultipartFile music;

    private MultipartFile cover;

    private MultipartFile video;

    @NotBlank
    private String name;

    @NotBlank
    private String singer;

    private String album;
}
