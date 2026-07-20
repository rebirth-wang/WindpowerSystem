package com.fastbee.common.extend.ai.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AI 问数语义忽略标记。
 *
 * <p>用于标记密码、密钥、内部控制字段等不应进入问数语义库的实体字段。</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AiSemanticIgnore {

    /**
     * 忽略原因。
     */
    String reason() default "";
}
