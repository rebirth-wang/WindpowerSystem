package com.fastbee.rule.enums;

import java.util.Objects;
import java.util.stream.Stream;

import com.yomahub.liteflow.common.ChainConstant;
import lombok.Getter;

/**
 * 生成的json中 type 属性, 关键字大写
 *
 */
@Getter
public enum ExpressParserEnum {

    THEN(ChainConstant.THEN, "THEN"),
    WHEN(ChainConstant.WHEN, "WHEN"),

    SWITCH(ChainConstant.SWITCH, "SWITCH"),
    FOR(ChainConstant.FOR, "FOR"),
    CATCH(ChainConstant.CATCH, "CATCH"),
    DO(ChainConstant.DO, "DO"),

    WHILE(ChainConstant.WHILE, "WHILE"),
    IF(ChainConstant.IF, "IF"),

    AND(ChainConstant.AND, "AND"),
    OR(ChainConstant.OR, "OR"),
    NOT(ChainConstant.NOT, "NOT"),

    CHAIN(ChainConstant.CHAIN, "CHAIN"),
    ;

    private String type;

    private String desc;

    ExpressParserEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static ExpressParserEnum of(String type) {
        Objects.requireNonNull(type);

        return Stream.of(values())
                .filter(bean -> bean.type.equals(type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(type + " not exists!"));
    }
}
