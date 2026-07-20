package com.fastbee.common.annotation;

import java.lang.annotation.*;

import com.fastbee.common.enums.DataSourceType;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
    DataSourceType value() default DataSourceType.master;
}
