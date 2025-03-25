package org.beethoven.lib;

import com.google.common.collect.Lists;
import org.beethoven.pojo.enums.StorageProvider;

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
            "audio/mpeg", "audio/wav", "audio/aac",
            "audio/vnd.dlna.adts", "audio/webm", "audio/x-m4a"
    );

    public static final List<String> SUPPORT_IMAGE_MIME = Lists.newArrayList(
            "image/png", "image/jpeg", "image/webp"
    );

    public static final String MUSIC_DIR = "music/";

    public static final String COVER_DIR = "cover/";

    public static final String AVATAR_DIR = "avatar/";

    public static final String SYSTEM = "system/";

    public static final String SHARDING_SIZE = "shardingSize";

    public static final String DEFAULT_MUSIC_COVER = "defaultMusicCover";

    public static final String ENDPOINT_PREFIX = "ENDPOINT";

    public static final int DEFAULT_SHARDING_SIZE = 256 * 1024;

    public static final String USER_DIR = System.getProperty("user.dir");

    public static final String DEFAULT_STORAGE = StorageProvider.MINIO.name();

    public static class PREFIX {
        public static final String CONFIG = "config:";

        public static final String GITHUB_CLIENT_SECRET = "oauth2:secret:github";

        public static final String USER_INFO = "user:info:";

        public static final String USER_ID = "user:id:";
    }
}
