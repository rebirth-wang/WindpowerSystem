package com.fastbee.common.extend.ai.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * AI 问数语义字段配置。
 *
 * <p>用于在实体字段上沉淀稳定的字典、枚举、值映射和关系提示等语义信息。
 * 问数语义库导出时可优先读取该注解，减少人工维护 Excel 字段映射。</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AiSemanticField {

    /**
     * 语义名称，默认使用字段说明。
     */
    String semanticName() default "";

    /**
     * 语义类型。
     */
    AiSemanticType semanticType() default AiSemanticType.DIMENSION;

    /**
     * 来源类型。
     */
    AiSemanticSourceType sourceType() default AiSemanticSourceType.MANUAL;

    /**
     * 来源编码。
     *
     * <p>DICT 类型填写字典类型；ENUM 类型填写枚举类全限定名；MANUAL/AUTO_COMMENT 可按需留空。</p>
     */
    String sourceCode() default "";

    /**
     * 关联提示。
     */
    String relationHint() default "";

    /**
     * 值映射，推荐格式为“中文=值”。
     */
    String[] valueMappings() default {};

    /**
     * 同义词。
     */
    String[] synonyms() default {};

    /**
     * 查询提示。
     */
    String queryHint() default "";

    /**
     * 备注。
     */
    String remark() default "";
}
