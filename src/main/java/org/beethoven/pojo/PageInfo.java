package org.beethoven.pojo;

import lombok.Data;

import java.util.List;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-21
 */

@Data
public class PageInfo<T> {

    private final long total;

    private final List<T> list;

    private PageInfo(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    public static <T> PageInfo<T> result(List<T> list, long total) {
        return new PageInfo<>(list, total);
    }
}
