package org.beethoven.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-30
 */

@ToString
@AllArgsConstructor
public class CommonVo<T1, T2> {

    public T1 k1;

    public T2 k2;
}
