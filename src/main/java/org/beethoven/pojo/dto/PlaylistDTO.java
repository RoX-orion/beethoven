package org.beethoven.pojo.dto;

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

    private String title;

    private String cover;
}
