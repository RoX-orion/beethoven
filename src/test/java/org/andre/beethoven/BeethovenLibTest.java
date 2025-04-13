package org.andre.beethoven;

import org.beethoven.lib.BeethovenLib;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-14
 */

@SpringBootTest
public class BeethovenLibTest {

    @Test
    public void getAudioDuration() {
        double duration = BeethovenLib.INSTANCE.get_duration("/home/andre/dingxi.mp3");
        System.out.println(duration);
    }
}
