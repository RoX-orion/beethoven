package org.beethoven.lib;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-14
 */

public interface BeethovenLib extends Library {

//        (Platform.isWindows() ? "msvcrt" : "libBeethovenLib")

    BeethovenLib INSTANCE = Native.load("BeethovenLib", BeethovenLib.class);

    int get_duration(String path);
}
