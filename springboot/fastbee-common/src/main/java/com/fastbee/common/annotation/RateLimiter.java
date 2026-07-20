package com.fastbee.common.annotation;

import java.lang.annotation.*;

import com.fastbee.common.enums.LimitType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    String key() default "rate_limit:";

    int time() default 60;

    int count() default 100;

    LimitType limitType() default LimitType.DEFAULT;
}
