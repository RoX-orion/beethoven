package org.beethoven.lib;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-02
 */

public class Constant {

    private Constant() {}

    public static final List<String> SUPPORT_AUDIO_MIME = Lists.newArrayList(
            "audio/mpeg", "audio/wav", "audio/aac"
    );

    public static final List<String> SUPPORT_IMAGE_MIME = Lists.newArrayList(
            "image/png", "image/jpeg"
    );

    public static final String MUSIC_DIR = "music/";

    public static final String COVER_DIR = "cover/";

    public static final String SHARDING_CONFIG_KEY = "sharding_size";

    public static final String OSS_DOMAIN = "oss_domain";

    public static final int DEFAULT_SHARDING_SIZE = 256 * 1024;

    public static final String USER_DIR = System.getProperty("user.dir");
}
