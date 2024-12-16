package org.beethoven.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.beethoven.pojo.enums.UserType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-16
 */

@Data
public class UserDTO {

    private String avatarUrl;

    private MultipartFile avatarFile;

    @NotBlank
    private String name;

    @Email
    private String email;

    private UserType userType;
}
