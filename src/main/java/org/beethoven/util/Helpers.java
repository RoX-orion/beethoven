package org.beethoven.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description: 辅助类
 * @author: Andre Lina
 * @date: 2024-10-09
 */

public class Helpers {

    private Helpers() {}

    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(threadLocalRandom.nextInt(10));
        }

        return sb.toString();
    }
}
