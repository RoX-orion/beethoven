package org.andre.beethoven;

import org.beethoven.util.Helpers;
import org.junit.Test;

import java.io.File;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-09
 */

public class FileTest {

    @Test
    public void fileInfo() {
        File file = new File("C:\\Users\\52828\\Desktop\\rain.mp3");
        System.out.println(file.getName());
    }

    @Test
    public void buildOssFileName() {
        System.out.println(Helpers.buildOssFileName("rain.mp4"));
    }
}
