package org.beethoven.util;

import org.beethoven.lib.Constant;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-02
 */

public class FileUtil {

    private FileUtil(){}

    public enum FileType {
        AUDIO,
        IMAGE,
        VIDEO
    }

    public static boolean checkMime(String mime, FileType fileType) {
        switch (fileType) {
            case AUDIO -> {
                return Constant.SUPPORT_AUDIO_MIME.contains(mime);
            }
            case IMAGE -> {
                return Constant.SUPPORT_IMAGE_MIME.contains(mime);
            }
            case VIDEO -> {
                return Constant.SUPPORT_VIDEO_MIME.contains(mime);
            }
            default -> {
                return false;
            }
        }
    }
}
