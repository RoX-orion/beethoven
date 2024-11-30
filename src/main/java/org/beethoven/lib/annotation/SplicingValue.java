package org.beethoven.lib.annotation;

import org.beethoven.pojo.enums.SplicingPosition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-20
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SplicingValue {

    String value();

    SplicingPosition position() default SplicingPosition.BEFORE;
}
