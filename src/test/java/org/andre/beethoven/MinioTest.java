package org.andre.beethoven;

import jakarta.annotation.Resource;
import org.beethoven.BeethovenApplication;
import org.beethoven.lib.store.MinIO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-20
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BeethovenApplication .class)
public class MinioTest {

    @Resource
    private MinIO minIO;

    @Test
    public void upload() throws FileNotFoundException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(""));
        minIO.upload(inputStream, "cc.png");
    }

    @Test
    public void getURL() {
        System.out.println(minIO.getURL("cover/694630672717708.png"));
    }
}
