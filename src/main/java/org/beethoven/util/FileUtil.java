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

    public static boolean checkAudioMime(String mime) {
        return Constant.SUPPORT_AUDIO_MIME.contains(mime);
    }

    public static boolean checkImageMime(String mime) {
        return Constant.SUPPORT_IMAGE_MIME.contains(mime);
    }
}
