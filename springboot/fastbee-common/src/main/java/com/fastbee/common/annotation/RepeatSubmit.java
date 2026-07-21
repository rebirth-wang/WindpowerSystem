package com.fastbee.common.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
    int interval() default 5000;

    String message() default "不允许重复提交，请稍候再试";
}
