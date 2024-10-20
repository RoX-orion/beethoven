package org.beethoven.pojo;

import lombok.Data;

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

    private final T list;

    private PageInfo(T list, long total) {
        this.list = list;
        this.total = total;
    }

    public static <T> PageInfo<T> result(T list, long total) {
        return new PageInfo<>(list, total);
    }
}
