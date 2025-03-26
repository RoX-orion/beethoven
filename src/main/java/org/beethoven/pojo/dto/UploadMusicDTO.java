package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-02
 */

@Data
public class UploadMusicDTO {

    @NotNull
    private MultipartFile music;

    @NotNull
    private MultipartFile cover;

    private MultipartFile video;

    @NotBlank
    private String name;

    @NotBlank
    private String singer;

    private String album;
}
