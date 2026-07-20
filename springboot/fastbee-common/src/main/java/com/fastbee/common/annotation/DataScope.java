package com.fastbee.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    String deptAlias() default "";

    String userAlias() default "";

    String permission() default "";

    String fieldAlias() default "";
}

