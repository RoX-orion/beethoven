package org.beethoven.pojo.vo;

import lombok.Data;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-07
 */

@Data
public class ManageMusic {

    public String id;

    public String name;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    public String link;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    public String cover;

    public String singer;

    public int duration;

    public int size;

    public String mime;

    private String uploader;

    private Integer uploaderId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
}
