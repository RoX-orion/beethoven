package org.beethoven.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-21
 */

@Data
public class PlaylistVo {

    private Integer id;

    private String author;

    private String title;

    private String cover;

    private int musicCount;

    private Date createTime;

    private Date updateTime;
}
