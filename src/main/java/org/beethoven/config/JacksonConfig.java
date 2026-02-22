package org.beethoven.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-01-12
 */

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule longToStringModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(long.class, ToStringSerializer.instance);
        module.addSerializer(BigInteger.class, ToStringSerializer.instance);

        return module;
    }
}
