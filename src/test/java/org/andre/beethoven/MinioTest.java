package org.andre.beethoven;

import jakarta.annotation.Resource;
import org.beethoven.BeethovenApplication;
import org.beethoven.lib.store.StorageContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
    private StorageContext storageContext;

    @Test
    public void upload() throws FileNotFoundException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("C:\\Users\\52828\\Desktop\\face.png"));
        storageContext.upload(inputStream, "andre.png");
    }

    @Test
    public void getURL() {
        System.out.println(storageContext.getURL("music/571560045282281.mp3"));
    }

    @Test
    public void downloadMedia() {
        try (InputStream inputStream = storageContext.download("music/571560045282281.mp3", 100L, null)) {
            byte[] bytes = inputStream.readAllBytes();
            System.out.println(bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeFile() {
        storageContext.remove("avatar/IMG_20241003_144845.jpg");
    }
}
