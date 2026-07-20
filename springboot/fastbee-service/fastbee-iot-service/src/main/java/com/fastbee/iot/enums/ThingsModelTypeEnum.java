package com.fastbee.iot.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import com.fastbee.common.utils.StringUtils;

/**
 * 物模型类别枚举
 * 对应type字段：1-属性，2-功能，3-事件
 */
@Getter
public enum ThingsModelTypeEnum {
    PROPERTY(1, "属性", Arrays.asList("属性", "1", "PROPERTY", "property")),
    FUNCTION(2, "功能", Arrays.asList("功能", "2", "FUNCTION", "function")),
    EVENT(3, "事件", Arrays.asList("事件", "3", "EVENT", "event"));

    /** 数据库存储的数字编码 */
    private final Integer code;
    /** 导出显示的标准文字 */
    private final String text;
    /** 导入支持的文字别名（匹配任意一个即可） */
    private final List<String> aliases;

    ThingsModelTypeEnum(Integer code, String text, List<String> aliases) {
        this.code = code;
        this.text = text;
        this.aliases = aliases;
    }

    /**
     * 根据导入的文字匹配编码（忽略大小写，支持别名）
     * @param text 模板中的文字/数字文字
     * @return 对应的数字编码
     * @throws IllegalArgumentException 匹配不到时抛出异常
     */
    public static Integer getCodeByText(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String matchText = text.trim();
        return Arrays.stream(values())
                .filter(enumObj -> enumObj.getAliases().contains(matchText))
                .findFirst()
                .map(ThingsModelTypeEnum::getCode)
                .orElseThrow(() -> new IllegalArgumentException("物模型类别不合法：" + text + "，支持值：属性/功能/事件"));
    }

    /**
     * 根据编码匹配标准文字（用于导出）
     * @param code 数据库数字编码
     * @return 导出显示的文字
     */
    public static String getTextByCode(Integer code) {
        if (code == null) {
            return "";
        }
        return Arrays.stream(values())
                .filter(enumObj -> enumObj.getCode().equals(code))
                .findFirst()
                .map(ThingsModelTypeEnum::getText)
                .orElse("");
    }

    /**
     * 获取所有支持的导入文字（用于提示）
     */
    public static String getAllSupportTexts() {
        return Arrays.stream(values())
                .map(enumObj -> enumObj.getAliases().toString())
                .collect(Collectors.joining(" | "));
    }
}
