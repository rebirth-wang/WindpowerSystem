package com.fastbee.common.annotation;

import java.lang.annotation.*;

import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.enums.OperatorType;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    String title() default "";

    BusinessType businessType() default BusinessType.OTHER;

    OperatorType operatorType() default OperatorType.MANAGE;

    boolean isSaveRequestData() default true;

    boolean isSaveResponseData() default true;
}
