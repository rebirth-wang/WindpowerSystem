package com.fastbee.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysProtocol {
    String name() default "";

    String protocolCode() default "";

    String description() default "";
}
