package org.beethoven.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-21
 */

@RestController
@RequestMapping("test")
public class TestController {


    @RequestMapping(value = "file", method = RequestMethod.POST)
    public void downloadFile(HttpServletResponse response) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("C:\\Users\\52828\\Desktop\\rain.mp3"))) {
            byte[] bytes = new byte[4096];
            ServletOutputStream outputStream = response.getOutputStream();
            int i;
            while ((i = bufferedInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
