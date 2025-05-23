package org.beethoven.util;

import jakarta.xml.bind.DatatypeConverter;
import org.beethoven.pojo.PageParam;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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

    public static String buildFuzzySearchParam(String param) {
        if (StringUtils.hasText(param)) {
            return "%" + param.trim() + "%";
        }

        return null;
    }

    public static String buildOssFileName(String fileName) {
        return getRandomString(15) + getFileExtensionName(fileName);
    }

    public static String getFileExtensionName(String fileName) {
        int i = fileName.indexOf('.');
        if (i != -1) {
            return fileName.substring(i);
        }
        return "";
    }

    private static MessageDigest sha(String type) {
        try {
            return MessageDigest.getInstance(type);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] concat(byte[]... bytes) {
        if (bytes.length == 1)
            return bytes[0];
        int length = 0;

        for (byte[] b : bytes) {
            for (int j = 0; j < b.length; j++) {
                length++;
            }
        }

        int i = 0;
        byte[] result = new byte[length];
        for (byte[] b : bytes) {
            for (byte value : b) {
                result[i++] = value;
            }
        }

        return result;
    }

    public static byte[] SHA1(byte[] ...data) {
        return sha("SHA").digest(concat(data));
    }

    public static byte[] SHA256(byte[] ...data) {
        return sha("SHA-256").digest(Helpers.concat(data));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] readBytesFromInt(int value) {
        byte[] result = new byte[4];
        result[3] = (byte) ((value >> 24) & 0xFF);
        result[2] = (byte) ((value >> 16) & 0xFF);
        result[1] = (byte) ((value >> 8) & 0xFF);
        result[0] = (byte) (value & 0xFF);
        return result;
    }

    public static Map<String, String> getBodyAsMap(String source) {
        Map<String, String> body = new HashMap<>();
        String[] split = source.split("&");
        for (String s : split) {
            String[] kv = s.split("=");
            if (kv.length == 2) {
                body.put(kv[0], kv[1]);
            } else {
                body.put(kv[0], null);
            }
        }
        return body;
    }

    public static PageParam buildPageParam(Integer page, Integer size) {
        if (page == null)
            return null;
        if (page <= 0)
            page = 1;
        if (size == null || size <= 0)
            size = 15;
        PageParam pageParam = new PageParam();
        pageParam.setSize(size);
        pageParam.setOffset((page - 1) * size);

        return pageParam;
    }

    public static String checksum(InputStream inputStream) {
        byte[] hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int i;
            while ((i = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, i);
            }
            hash = digest.digest();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // 将 hash 转为十六进制字符串
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }

//    public static String buildFullOssLink(String uri) {
//        return GlobalConfig.endpoint + uri;
//    }

//    public static String[] calculateHash(InputStream inputStream, int shardingSize) {
//        int len = 0;
//        int i, start, end;
//        byte[] buffer = new byte[BUFFER_SIZE];
//        int totalLen = inputStream.available();
//        int capacity = totalLen / shardingSize;
//        if (capacity % shardingSize == 0) capacity += 1;
//        else capacity += 2;
//
//        String[] sha = new String[capacity];
//        while ((i = inputStream.read(buffer)) != -1) {
//            if (shardingSize < BUFFER_SIZE) {
//
//            } else {
//
//            }
//        }
//    }
}
